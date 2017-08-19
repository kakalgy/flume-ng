package com.flume.core.lifecycle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.sdk.FlumeException;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 生命周期管理员
 * 
 * <p>
 * Flume可以监控并管理组件的运行状态，在组件关闭的时候可以自动拉起来，原理是通过启动一个计划任务线程池（monitorService，线程的最大数量为30），
 * 运行监控线程（MonitorRunnable线程），每隔3s判断组件(包括Channel，SinkRunner)的状态是否符合要求(可用的状态由两种START和STOP)，
 * 根据不同的要求调用对应组件不同的方法，START会调用start方法，STOP会调用stop方法，如果想监控一个组件的状态，只需对这个组件调用supervise方法即可，如果想停止监控一个组件，
 * 只需对这个组件调用unsupervise方法即可，同时有一个线程每隔两小时移除已经不再监控（调用了unsupervise方法）的组件的检查任务。
 * </p>
 * 
 * @author
 *
 */
public class LifecycleSupervisor implements LifecycleAware {

	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(LifecycleSupervisor.class);

	/**
	 * Flume的所有组件,用于存放被守护的组件
	 * <p>
	 * supervisedProcesses 用于存放LifecycleAware和Supervisoree对象的键值对，代表已经管理的组件
	 * </p>
	 */
	private Map<LifecycleAware, Supervisoree> supervisedProcesses;// Supervisoree是内部类
	/**
	 * 它用于表示ScheduledExecutorService中提交了任务的返回结果。我们通过Delayed的接口getDelay()方法知道该任务还有好久才被执行。
	 * <p>
	 * 用于存放正在被守护的组件，与上面的supervisedProcesses是对应的，ScheduledFuture是该组件的监控线程
	 * </p>
	 */
	private Map<LifecycleAware, ScheduledFuture<?>> monitorFurures;

	/**
	 * <p>
	 * 创建监控服务线程池
	 * </p>
	 * <p>
	 * Java提供的Time类可以周期性地或者延期执行任务，但是有时我们需要并行执行同样的任务，这个时候如果创建多个Time对象会给系统带来负担，解决办法是将定时任务放到线程池中执行。
	 * </p>
	 * <p>
	 * Java的ScheduledThreadPoolExecutor类实现了ScheduledExecutorService接口中定义的以不同方法执行任务的方法。
	 * </p>
	 * <p>
	 * http://www.importnew.com/7276.html
	 * </p>
	 */
	private ScheduledThreadPoolExecutor monitorService;// 线程池

	/**
	 * Flume组件管理中心的状态
	 */
	private LifecycleState lifecycleState;
	/**
	 * 定期清理被取消的组件
	 */
	private Purger purger;// 清除线程
	private boolean needToPurger;// 是否需要清除

	/**
	 * 构造函数
	 */
	public LifecycleSupervisor() {
		// TODO Auto-generated constructor stub
		lifecycleState = LifecycleState.IDLE;
		supervisedProcesses = new HashMap<LifecycleAware, LifecycleSupervisor.Supervisoree>();
		monitorFurures = new HashMap<LifecycleAware, ScheduledFuture<?>>();
		monitorService = new ScheduledThreadPoolExecutor(10,
				new ThreadFactoryBuilder().setNameFormat("lifecycleSupervisor-" + Thread.currentThread().getId() + "-%d").build());
		monitorService.setMaximumPoolSize(20);
		monitorService.setKeepAliveTime(30, TimeUnit.SECONDS);
		purger = new Purger();
		needToPurger = false;

	}

	/**
	 * 开启生命周期
	 */
	public synchronized void start() {
		// TODO Auto-generated method stub
		logger.info("Starting lifecycle supervisor {}", Thread.currentThread().getId());

		// 每隔两小时检查一次
		monitorService.scheduleWithFixedDelay(purger, 2, 2, TimeUnit.HOURS);
		lifecycleState = LifecycleState.START;

		logger.debug("Lifecycle supervisor started");
	}

	/**
	 * 停止生命周期
	 */
	public void stop() {
		// TODO Auto-generated method stub
		logger.info("Stopping lifecycle supervisor {}", Thread.currentThread().getId());

		if (monitorService != null) {
			/**
			 * 当线程池调用该方法时,线程池的状态则立刻变成SHUTDOWN状态。此时，则不能再往线程池中添加任何任务，否则将会抛出RejectedExecutionException异常。但是，此时线程池不会立刻退出，直到添加到线程池中的任务都已经处理完成，才会退出。
			 */
			monitorService.shutdown();// 关闭任务，

			try {
				monitorService.awaitTermination(10, TimeUnit.SECONDS);// 等待10秒关闭monitorService
			} catch (InterruptedException e) {
				// TODO: handle exception
				logger.error("Interrupted while waiting for monitor service to stop");
			}

			if (!monitorService.isTerminated()) {
				/**
				 * 执行该方法，线程池的状态立刻变成STOP状态，并试图停止所有正在执行的线程，不再处理还在池队列中等待的任务，当然，它会返回那些未执行的任务。
				 */
				monitorService.shutdownNow();
				try {
					while (!monitorService.isTerminated()) {
						monitorService.awaitTermination(10, TimeUnit.SECONDS);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error("Interrupted while waiting for monitor service to stop");
				}
			}
		}

		// 遍历supervisedProcesses中的各个组件
		for (final Entry<LifecycleAware, Supervisoree> entry : supervisedProcesses.entrySet()) {
			// 如果组件的当前状态是START，则首先设置其需要变成的状态为STOP，并调用组件的stop方法
			if (entry.getKey().getLifecycleState().equals(LifecycleState.START)) {
				entry.getValue().status.desiredState = LifecycleState.STOP;
				entry.getKey().stop();
			}
		}
		/* If we've failed, preserve the error state. */
		if (lifecycleState.equals(LifecycleState.START)) {
			lifecycleState = LifecycleState.STOP;
		}

		// 清楚map中的数据
		supervisedProcesses.clear();
		monitorFurures.clear();
		logger.debug("Lifecycle supervisor stopped");
	}

	public synchronized LifecycleState getLifecycleState() {
		// TODO Auto-generated method stub
		return this.lifecycleState;
	}

	/**
	 * 失败情况，将lifecycleState状态置为ERROR
	 */
	public synchronized void fail() {
		lifecycleState = LifecycleState.ERROR;
	}

	/**
	 * 监控对应的组件
	 * 
	 * @param lifecycleAware
	 * @param policy
	 * @param desiredState
	 */
	public synchronized void supervise(LifecycleAware lifecycleAware, SupervisorPolicy policy, LifecycleState desiredState) {
		// 检测监控线程池是否正常
		if (this.monitorService.isShutdown() || this.monitorService.isTerminated() || this.monitorService.isTerminating()) {
			throw new FlumeException("Supervise called on " + lifecycleAware + " " + "after shutdown has been initiated. " + lifecycleAware
					+ " will not" + " be started");
		}
		// 检测是否已经管理
		Preconditions.checkState(!supervisedProcesses.containsKey(lifecycleAware),
				"Refusing to supervise " + lifecycleAware + " more than once");

		if (logger.isDebugEnabled()) {
			logger.debug("Supervising service:{} policy:{} desiredState:{}", new Object[] { lifecycleAware, policy, desiredState });
		}

		Supervisoree process = new Supervisoree();// 初始化Supervisoree对象
		process.status = new Status();// 并实例化Supervisoree对象的Status属性

		process.policy = policy;// 设置Supervisoree的属性
		process.status.desiredState = desiredState;
		process.status.error = false;

		// 初始化一个MonitorRunnable 对象（线程），并设置对象的属性
		MonitorRunnable monitorRunnable = new MonitorRunnable();
		monitorRunnable.lifecycleAware = lifecycleAware;
		monitorRunnable.supervisoree = process;
		monitorRunnable.monitorService = monitorService;

		supervisedProcesses.put(lifecycleAware, process);// 向supervisedProcesses中插入键值对，代表已经开始管理的组件

		// 设置计划任务线程池，每隔3s之后运行monitorRunnable
		ScheduledFuture<?> future = monitorService.scheduleWithFixedDelay(monitorRunnable, 0, 3, TimeUnit.SECONDS);
		// 向monitorFutures中插入键值对
		monitorFurures.put(lifecycleAware, future);
	}

	/**
	 * 停止组件并从监控容器中去除
	 * 
	 * @param lifecycleAware
	 */
	public synchronized void unsupervise(LifecycleAware lifecycleAware) {
		Preconditions.checkState(supervisedProcesses.containsKey(lifecycleAware),
				"Unaware of " + lifecycleAware + " - can not unsupervise");

		logger.debug("Unsupervising service:{}", lifecycleAware);

		synchronized (lifecycleAware) {
			Supervisoree supervisoree = supervisedProcesses.get(lifecycleAware);// 从已经管理的Supervisoree的hashmap中获取Supervisoree对象
			supervisoree.status.discard = true;// 设置Supervisoree对象的Status属性的discard的值为discard
			// 调用setDesiredState方法，设置Supervisoree对象的Status属性的desiredState值为STOP（supervisoree.status.desiredState=desiredState）
			this.setDesiredState(lifecycleAware, LifecycleState.STOP);
			logger.info("Stopping component: {}", lifecycleAware);
			lifecycleAware.stop();// 调用组件的stop方法
		}

		supervisedProcesses.remove(lifecycleAware);// 从supervisedProcesses的hashmap中移除这个组件
		// We need to do this because a reconfiguration simply unsupervises old
		// components and supervises new ones.
		monitorFurures.get(lifecycleAware).cancel(false);
		// purges are expensive, so it is done only once every 2 hours.
		needToPurger = true;// 设置needToPurge的属性为true，这样就可以在purge中删除已经cancel的ScheduledFuture对象，从monitorService线程池内移除取消状态的任务
		monitorFurures.remove(lifecycleAware);
	}

	public synchronized void setDesiredState(LifecycleAware lifecycleAware, LifecycleState desiredState) {
		Preconditions.checkState(supervisedProcesses.containsKey(lifecycleAware),
				"Unaware of " + lifecycleAware + " - can not set desired state to " + desiredState);

		logger.debug("Setting desiredState:{} on service:{}", desiredState, lifecycleAware);

		Supervisoree supervisoree = supervisedProcesses.get(lifecycleAware);
		supervisoree.status.desiredState = desiredState;
	}

	/**
	 * 返回LifecycleAware对象 是否是错误状态
	 * 
	 * @param compnent
	 * @return
	 */
	public synchronized boolean isComponentInErrorState(LifecycleAware compnent) {
		return supervisedProcesses.get(compnent).status.error;
	}

	/**
	 * Supervisoree的状态属性类
	 * 
	 * @author
	 *
	 */
	public static class Status {
		public Long firstSeen;// Supervisoree第一次检查时的时间
		public Long lastSeen;// Supervisoree上一次检查时的时间
		public LifecycleState lastSeenState;
		public LifecycleState desiredState;
		public int failures;
		public boolean discard;// 丢弃，抛弃
		public volatile boolean error;// 不保证原子性

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "{ lastSeen:" + lastSeen + " lastSeenState:" + lastSeenState + " desiredState:" + desiredState + " firstSeen:"
					+ firstSeen + " failures:" + failures + " discard:" + discard + " error:" + error + " }";
		}
	}

	/**
	 * <p>
	 * 理解：Supervisoree的策略属性，主要是根据isValid()函数来判断
	 * </p>
	 * <p>
	 * SupervisorPolicy 是抽象类，定义了抽象方法isValid(LifecycleAware object, Status
	 * status)，包含两个扩展类AlwaysRestartPolicy 和OnceOnlyPolicy AlwaysRestartPolicy
	 * 的isValid会一直返回true，OnceOnlyPolicy 的isValid的方法会判断Status.failures
	 * 的值，如果为0则返回true,否则返回false
	 * </p>
	 * <p>
	 * Supervisoree包含SupervisorPolicy 和Status属性
	 * </p>
	 * 
	 * @author
	 *
	 */
	public abstract static class SupervisorPolicy {
		abstract boolean isValid(LifecycleAware object, Status status);

		public static class AlwaysRestartPolicy extends SupervisorPolicy {
			@Override
			boolean isValid(LifecycleAware object, Status status) {
				// TODO Auto-generated method stub
				return true;
			}
		}

		public static class OnceOnlyPolicy extends SupervisorPolicy {
			@Override
			boolean isValid(LifecycleAware object, Status status) {
				// TODO Auto-generated method stub
				return status.failures == 0;
			}
		}

	}

	/**
	 * Flume管理组件的守护组件,包含SupervisorPolicy 和Status属性
	 * 
	 * @author
	 *
	 */
	private static class Supervisoree {
		public SupervisorPolicy policy;
		public Status status;

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "{ status:" + status + " policy:" + policy + " }";
		}
	}

	/**
	 * 定期清理被取消的组件，实现Runnable接口，run()方法用来清除组件，再将needToPurger置为false
	 * 
	 * @author
	 *
	 */
	private class Purger implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			if (needToPurger) {
				monitorService.purge();
				needToPurger = false;
			}
		}
	}

	/**
	 * 监控线程，实现Runnable接口
	 * 
	 * @author Administrator
	 *
	 */
	public static class MonitorRunnable implements Runnable {
		public ScheduledExecutorService monitorService;// 在类中未使用，应该是在调用的时候使用
		public LifecycleAware lifecycleAware;//
		public Supervisoree supervisoree;

		public void run() {
			// TODO Auto-generated method stub
			logger.debug("checking process:{} supervisoree:{}", lifecycleAware, supervisoree);

			long now = System.currentTimeMillis();// 获取当前的时间戳
			try {
				if (supervisoree.status.firstSeen == null) {
					logger.debug("first time seeing {}", lifecycleAware);
					// 如果这个组件是是初次受监控
					supervisoree.status.firstSeen = now;
				}
				supervisoree.status.lastSeen = now;
				synchronized (lifecycleAware) {// 锁住组件
					if (supervisoree.status.discard) {// 该组件已经停止监控
						// Unsupervise has already been called on this.
						logger.info("Component has already been stopped {}", lifecycleAware);
						return;// 直接返回
					} else {
						if (supervisoree.status.error) {// 该组件是错误状态
							logger.info("Component {} is in error state, and Flume will not" + "attempt to change its state",
									lifecycleAware);
							return;// 直接返回
						}
					}
					// 获取组件最新状态,没运行start()方法之前是LifecycleState.IDLE状态
					supervisoree.status.lastSeenState = lifecycleAware.getLifecycleState();

					// 该组件最新状态和期望的状态不一致
					if (!lifecycleAware.getLifecycleState().equals(supervisoree.status.desiredState)) {
						logger.debug("Want to transition {} from {} to {} (failures:{})", new Object[] { lifecycleAware,
								supervisoree.status.lastSeenState, supervisoree.status.desiredState, supervisoree.status.failures });

						switch (supervisoree.status.desiredState) {// 根据期望状态执行相应的操作
						case START:
							try {
								lifecycleAware.start();
							} catch (Throwable t) {
								// TODO: handle exception
								logger.error("Unable to start " + lifecycleAware + " - Exception follows.", t);
								if (t instanceof Error) {
									// This component can never recover, shut it
									// down.
									supervisoree.status.desiredState = LifecycleState.STOP;
									try {
										lifecycleAware.stop();
										logger.warn("Component {} stopped, since it could not be"
												+ "successfully started due to missing dependencies", lifecycleAware);
									} catch (Throwable e1) {
										logger.error("Unsuccessful attempt to " + "shutdown component: {} due to missing dependencies."
												+ " Please shutdown the agent" + "or disable this component, or the agent will be"
												+ "in an undefined state.", e1);
										supervisoree.status.error = true;
										if (e1 instanceof Error) {
											throw (Error) e1;
										}
										// Set the state to stop, so that the
										// conf
										// poller can
										// proceed.
									}
								}
								supervisoree.status.failures++;// 启动错误失败次数+1
							}
							break;
						case STOP:
							try {
								lifecycleAware.stop();// 停止组件
							} catch (Throwable t) {
								// TODO: handle exception
								logger.error("Unable to stop " + lifecycleAware + " - Exception follows.", t);
								if (t instanceof Error) {
									throw (Error) t;
								}
								supervisoree.status.failures++;// 组件停止错误，错误次数+1
							}
						default:
							logger.warn("I refuse to acknowledge {} as a desired state", supervisoree.status.desiredState);
						}

						// 两种SupervisorPolicy(AlwaysRestartPolicy和OnceOnlyPolicy)后者还未使用过，前者表示可以重新启动的组件，后者表示只能运行一次的组件

						// 比如OnceOnlyPolicy
						// 的isValid的方法会判断Status.failures的值，如果为0则返回true,否则返回false
						if (!supervisoree.policy.isValid(lifecycleAware, supervisoree.status)) {
							logger.error("Policy {} of {} has been violated - supervisor should exit!", supervisoree.policy,
									lifecycleAware);
						}
					}
				}
			} catch (Throwable t) {
				// TODO: handle exception
				logger.error("Unexpected error", t);
			}
			logger.debug("Status check complete");
		}
	}

}

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
 * @author
 *
 */
public class LifecycleSupervisor implements LifecycleAware {

	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(LifecycleSupervisor.class);

	private Map<LifecycleAware, Supervisoree> supervisedProcesses;// Supervisoree是内部类
	private Map<LifecycleAware, ScheduledFuture<?>> monitorFurures;

	/**
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
	private ScheduledThreadPoolExecutor monitorService;

	private LifecycleState lifecycleState;
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

		for (final Entry<LifecycleAware, Supervisoree> entry : supervisedProcesses.entrySet()) {
			if (entry.getKey().getLifecycleState().equals(LifecycleState.START)) {
				entry.getValue().status.desiredState = LifecycleState.STOP;
				entry.getKey().stop();
			}
		}
		/* If we've failed, preserve the error state. */
		if (lifecycleState.equals(LifecycleState.START)) {
			lifecycleState = LifecycleState.STOP;
		}

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
	 * 
	 * @param lifecycleAware
	 * @param policy
	 * @param desiredState
	 */
	public synchronized void supervise(LifecycleAware lifecycleAware, SupervisorPolicy policy, LifecycleState desiredState) {
		if (this.monitorService.isShutdown() || this.monitorService.isTerminated() || this.monitorService.isTerminating()) {
			throw new FlumeException("Supervise called on " + lifecycleAware + " " + "after shutdown has been initiated. " + lifecycleAware
					+ " will not" + " be started");
		}

		Preconditions.checkState(!supervisedProcesses.containsKey(lifecycleAware),
				"Refusing to supervise " + lifecycleAware + " more than once");

		if (logger.isDebugEnabled()) {
			logger.debug("Supervising service:{} policy:{} desiredState:{}", new Object[] { lifecycleAware, policy, desiredState });
		}

		Supervisoree process = new Supervisoree();
		process.status = new Status();

		process.policy = policy;
		process.status.desiredState = desiredState;
		process.status.error = false;

		MonitorRunnable monitorRunnable = new MonitorRunnable();
		monitorRunnable.lifecycleAware = lifecycleAware;
		monitorRunnable.supervisoree = process;
		monitorRunnable.monitorService = monitorService;

		supervisedProcesses.put(lifecycleAware, process);

		ScheduledFuture<?> future = monitorService.scheduleWithFixedDelay(monitorRunnable, 0, 3, TimeUnit.SECONDS);
		monitorFurures.put(lifecycleAware, future);
	}

	public synchronized void unsupervise(LifecycleAware lifecycleAware) {
		Preconditions.checkState(supervisedProcesses.containsKey(lifecycleAware),
				"Unaware of " + lifecycleAware + " - can not unsupervise");

		logger.debug("Unsupervising service:{}", lifecycleAware);

		synchronized (lifecycleAware) {
			Supervisoree supervisoree = supervisedProcesses.get(lifecycleAware);
			supervisoree.status.discard = true;
			this.setDesiredState(lifecycleAware, LifecycleState.STOP);
			logger.info("Stopping component: {}", lifecycleAware);
			lifecycleAware.stop();
		}

		supervisedProcesses.remove(lifecycleAware);
		// We need to do this because a reconfiguration simply unsupervises old
		// components and supervises new ones.
		monitorFurures.get(lifecycleAware).cancel(false);
		// purges are expensive, so it is done only once every 2 hours.
		needToPurger = true;
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
	 * 
	 * @author Administrator
	 *
	 */
	public static class MonitorRunnable implements Runnable {
		public ScheduledExecutorService monitorService;
		public LifecycleAware lifecycleAware;
		public Supervisoree supervisoree;

		public void run() {
			// TODO Auto-generated method stub
			logger.debug("checking process:{} supervisoree:{}", lifecycleAware, supervisoree);

			long now = System.currentTimeMillis();
			try {
				if (supervisoree.status.firstSeen == null) {
					logger.debug("first time seeing {}", lifecycleAware);
					supervisoree.status.firstSeen = now;
				}
				supervisoree.status.lastSeen = now;
				synchronized (lifecycleAware) {
					if (supervisoree.status.discard) {
						// Unsupervise has already been called on this.
						logger.info("Component has already been stopped {}", lifecycleAware);
						return;
					} else {
						if (supervisoree.status.error) {
							logger.info("Component {} is in error state, and Flume will not" + "attempt to change its state",
									lifecycleAware);
							return;
						}
					}

					supervisoree.status.lastSeenState = lifecycleAware.getLifecycleState();

					if (!lifecycleAware.getLifecycleState().equals(supervisoree.status.desiredState)) {
						logger.debug("Want to transition {} from {} to {} (failures:{})", new Object[] { lifecycleAware,
								supervisoree.status.lastSeenState, supervisoree.status.desiredState, supervisoree.status.failures });

						switch (supervisoree.status.desiredState) {
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
								supervisoree.status.failures++;
							}
							break;
						case STOP:
							try {
								lifecycleAware.stop();
							} catch (Throwable t) {
								// TODO: handle exception
								logger.error("Unable to stop " + lifecycleAware + " - Exception follows.", t);
								if (t instanceof Error) {
									throw (Error) t;
								}
								supervisoree.status.failures++;
							}
						default:
							logger.warn("I refuse to acknowledge {} as a desired state", supervisoree.status.desiredState);
						}

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

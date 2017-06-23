package com.flume.core.lifecycle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public LifecycleState getLifecycleState() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @author
	 *
	 */
	public static class Status {
		public Long firstSeen;
		public Long lastSeen;
		public LifecycleState lastSeenState;
		public LifecycleState desiredState;
		public int failures;
		public boolean discard;// 丢弃，抛弃
		public volatile boolean error;// 不保证原子性

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "{ lastSeen:" + lastSeen + " lastSeenState:" + lastSeenState + " desiredState:" + desiredState + " firstSeen:" + firstSeen
					+ " failures:" + failures + " discard:" + discard + " error:" + error + " }";
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

}

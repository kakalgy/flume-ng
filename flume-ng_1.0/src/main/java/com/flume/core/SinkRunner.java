package com.flume.core;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.core.lifecycle.LifecycleAware;
import com.flume.core.lifecycle.LifecycleState;
import com.flume.sdk.EventDeliveryException;

/**
 * <p>
 * A driver for {@linkplain Sink sinks} that polls them, attempting to
 * {@linkplain Sink#process() process} events if any are available in the
 * {@link Channel}.
 * </p>
 *
 * <p>
 * Note that, unlike {@linkplain Source sources}, all sinks are polled.
 * </p>
 *
 * @see org.apache.flume.Sink
 * @see org.apache.flume.SourceRunner
 */
public class SinkRunner implements LifecycleAware {

	private static final Logger LOG = LoggerFactory.getLogger(SinkRunner.class);

	private static final long backoffSleepIncrement = 1000;
	private static final long maxBackoffSleep = 5000;

	private CounterGroup counterGroup;
	private PollingRunner pollingRunner;
	private Thread runnerThread;
	private LifecycleState lifecycleState;

	private SinkProcessor policy;

	/**
	 * 构造函数
	 */
	public SinkRunner() {
		// TODO Auto-generated constructor stub
		this.counterGroup = new CounterGroup();
		this.lifecycleState = LifecycleState.IDLE;
	}

	/**
	 * 构造函数
	 * 
	 * @param policy
	 */
	public SinkRunner(SinkProcessor policy) {
		this();
		this.setPolicy(policy);
	}

	/**
	 * 使用的前提是policy需要被初始化
	 */
	@Override
	public void start() {
		// TODO Auto-generated method stub
		SinkProcessor policy = this.getPolicy();

		policy.start();
		this.pollingRunner = new PollingRunner();
		this.pollingRunner.policy = this.policy;
		this.pollingRunner.counterGroup = this.counterGroup;
		this.pollingRunner.shouldStop = new AtomicBoolean();

		this.runnerThread = new Thread(this.pollingRunner);
		this.runnerThread.setName("SinkRunner-PollingRunner-" + this.policy.getClass().getSimpleName());
		this.runnerThread.start();

		this.lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (this.runnerThread != null) {
			this.pollingRunner.shouldStop.set(true);
			this.runnerThread.interrupt();

			while (this.runnerThread.isAlive()) {
				try {
					LOG.debug("Waiting for runner thread to exit");
					this.runnerThread.join(500);
				} catch (InterruptedException e) {
					// TODO: handle exception
					LOG.debug("Interrupted while waiting for runner thread to exit. Exception follows.", e);
				}
			}
		}

		this.getPolicy().stop();
		this.lifecycleState = LifecycleState.STOP;
	}

	@Override
	public LifecycleState getLifecycleState() {
		// TODO Auto-generated method stub
		return this.lifecycleState;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SinkRunner: { policy:" + this.getPolicy() + " counterGroup:" + this.counterGroup + " }";
	}

	/**
	 * {@link Runnable} that {@linkplain SinkProcessor#process() polls} a
	 * {@link SinkProcessor} and manages event delivery notification,
	 * {@link Sink.Status BACKOFF} delay handling, etc.
	 */
	public static class PollingRunner implements Runnable {

		private SinkProcessor policy;
		private AtomicBoolean shouldStop;
		private CounterGroup counterGroup;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			LOG.debug("Polling sink runner starting");

			while (!this.shouldStop.get()) {
				try {
					if (this.policy.process().equals(Sink.Status.BACKOFF)) {
						this.counterGroup.incrementAndGet("runner.backoffs");

						Thread.sleep(Math.min(this.counterGroup.incrementAndGet("runner.backoffs.consecutive") * backoffSleepIncrement,
								maxBackoffSleep));
					} else {
						this.counterGroup.set("runner.backoffs.consecutive", 0L);
					}
				} catch (InterruptedException e) {
					LOG.debug("Interrupted while processing an event. Exiting.");
					counterGroup.incrementAndGet("runner.interruptions");
				} catch (Exception e) {
					LOG.error("Unable to deliver event. Exception follows.", e);
					if (e instanceof EventDeliveryException) {
						counterGroup.incrementAndGet("runner.deliveryErrors");
					} else {
						counterGroup.incrementAndGet("runner.errors");
					}
					try {
						Thread.sleep(maxBackoffSleep);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}

			LOG.debug("Polling runner exiting. Metrics:{}", counterGroup);
		}
	}

	/******************************* Get/Set方法 ************************************/
	public SinkProcessor getPolicy() {
		return policy;
	}

	public void setPolicy(SinkProcessor policy) {
		this.policy = policy;
	}
}

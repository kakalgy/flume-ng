package com.flume.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.core.lifecycle.LifecycleAware;
import com.flume.core.lifecycle.LifecycleState;

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
		this.pollingRunner.
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public LifecycleState getLifecycleState() {
		// TODO Auto-generated method stub
		return null;
	}

	public static class PollingRunner implements Runnable {

		private SinkProcessor policy;
	}

	/******************************* Get/Set方法 ************************************/
	public SinkProcessor getPolicy() {
		return policy;
	}

	public void setPolicy(SinkProcessor policy) {
		this.policy = policy;
	}
}

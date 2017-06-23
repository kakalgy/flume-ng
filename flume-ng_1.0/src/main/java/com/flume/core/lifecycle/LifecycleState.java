package com.flume.core.lifecycle;

/**
 * 生命周期状态
 * 
 * @author
 *
 */
public enum LifecycleState {
	IDLE, START, STOP, ERROR;

	public static final LifecycleState[] START_OR_ERROR = new LifecycleState[] { START, ERROR };

	public static final LifecycleState[] STOP_OR_ERROR = new LifecycleState[] { STOP, ERROR };
}

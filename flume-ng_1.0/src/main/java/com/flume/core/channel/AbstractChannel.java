package com.flume.core.channel;

import com.flume.configuration.Context;
import com.flume.core.Channel;
import com.flume.core.conf.Configurable;
import com.flume.core.lifecycle.LifecycleAware;
import com.flume.core.lifecycle.LifecycleState;

/**
 * 
 * @Description
 * @author Administrator
 * @date   2017年8月3日 下午3:02:02
 *
 */
public abstract class AbstractChannel implements Channel, LifecycleAware, Configurable {
	/**
	 * Channel的名称
	 */
	private String name;

	private LifecycleState lifecycleState;// Channel的生命周期状态

	/**
	 * 
	 * 构造函数
	 */
	public AbstractChannel() {
		// TODO Auto-generated constructor stub
		this.lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public synchronized void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public synchronized String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		this.lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		this.lifecycleState = LifecycleState.STOP;
	}

	@Override
	public LifecycleState getLifecycleState() {
		// TODO Auto-generated method stub
		return this.lifecycleState;
	}

	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getName() + "{name: " + " }";
	}
}

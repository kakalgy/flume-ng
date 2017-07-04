package com.flume.core.source;

import com.flume.core.Source;
import com.flume.core.SourceRunner;
import com.flume.core.channel.ChannelProcessor;
import com.flume.core.lifecycle.LifecycleState;

/**
 * Starts, stops, and manages {@linkplain EventDrivenSource event-driven
 * sources}.
 */
public class EventDrivenSourceRunner extends SourceRunner {

	private LifecycleState lifecycleState;

	public EventDrivenSourceRunner() {
		// TODO Auto-generated constructor stub
		this.lifecycleState = LifecycleState.IDLE;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		Source source = this.getSource();
		ChannelProcessor cp = source.getChannelProcessor();
		cp.initialize();
		source.start();
		this.lifecycleState = LifecycleState.START;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		Source source = this.getSource();
		source.stop();
		ChannelProcessor cp = source.getChannelProcessor();
		cp.close();
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
		return "EventDrivenSourceRunner: { source:" + this.getSource() + " }";
	}
}

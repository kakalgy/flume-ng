package com.flume.core;

import com.flume.core.annotations.InterfaceAudience;
import com.flume.core.annotations.InterfaceStability;
import com.flume.core.channel.ChannelProcessor;
import com.flume.core.lifecycle.LifecycleAware;

/**
 * Source接口首先继承了LifecycleAware接口，然后只提供了ChannelProcessor的setter和getter接口，也就是说它的的所有逻辑的实现应该在LifecycleAware接口的start和stop中实现
 * <p>
 * <p>
 * A source generates {@plainlink Event events} and calls methods on the
 * configured {@link ChannelProcessor} to persist those events into the
 * configured {@linkplain Channel channels}.
 * </p>
 *
 * <p>
 * Sources are associated with unique {@linkplain NamedComponent names} that can
 * be used for separating configuration and working namespaces.
 * </p>
 *
 * <p>
 * No guarantees are given regarding thread safe access.
 * </p>
 *
 * @see org.apache.flume.Channel
 * @see org.apache.flume.Sink
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Source extends LifecycleAware, NamedComponent {

	/**
	 * Specifies which channel processor will handle this source's events.
	 * <p>
	 * 设置处理source中event的ChannelProcessor
	 *
	 * @param channelProcessor
	 */
	public void setChannelProcessor(ChannelProcessor channelProcessor);

	/**
	 * Returns the channel processor that will handle this source's events.
	 * <p>
	 * 返回处理source中event的ChannelProcessor
	 */
	public ChannelProcessor getChannelProcessor();
}

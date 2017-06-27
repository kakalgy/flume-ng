package com.flume.core;

import java.util.List;

import com.flume.core.conf.Configurable;
import com.flume.sdk.Event;

/**
 * <p>
 * Allows the selection of a subset(子集) of channels from the given set based on
 * its implementation policy. Different implementations of this interface
 * embody(表现、象征、包含) different policies that affect the choice of channels that a
 * source will push the incoming events to.
 * </p>
 * <p>
 * 根据其实现策略，允许从给定集合中选择一个channel的子集。
 * 此接口的不同实现体现了不同的策略会影响一个source推送event的时候对channel的选择
 * </p>
 */
public interface ChannelSelector extends NamedComponent, Configurable {

	/**
	 * @param channels
	 *            all channels the selector could select from.
	 */
	public void setChannels(List<Channel> channels);

	/**
	 * Returns a list of required channels. A failure in writing the event to
	 * these channels must be communicated back to the source that received this
	 * event.
	 * 
	 * @param event
	 * @return the list of required channels that this selector has selected for
	 *         the given event.
	 */
	public List<Channel> getRequiredChannels(Event event);

	/**
	 * Returns a list of optional channels. A failure in writing the event to
	 * these channels must be ignored.
	 * 
	 * @param event
	 * @return the list of optional channels that this selector has selected for
	 *         the given event.
	 */
	public List<Channel> getOptionalChannels(Event event);

	/**
	 * @return the list of all channels that this selector is configured to work
	 *         with.
	 */
	public List<Channel> getAllChannels();
}

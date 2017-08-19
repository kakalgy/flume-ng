package com.flume.configuration.conf.channel;

/**
 * Enumeration of built in channel selector types available in the system.
 * <p/>
 * channel selector的类型(Channel选择器的类型)
 * <p>
 * 默认实现是复制选择器ReplicatingChannelSelector，即把接收到的消息复制到每一个Channel；多路复用选择器MultiplexingChannelSelector会根据Event
 * Header中的参数进行选择，以此来选择使用哪个Channel。
 */
public enum ChannelSelectorType {
	/**
	 * Place holder for custom channel selectors not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * Replicating(复制) channel selector.
	 */
	REPLICATING("org.apache.flume.channel.ReplicatingChannelSelector"),

	/**
	 * Multiplexing(多路传输) channel selector.
	 */
	MULTIPLEXING("org.apache.flume.channel.MultiplexingChannelSelector");

	private final String channelSelectorClassName;

	/**
	 * 构造函数
	 * 
	 * @param channelSelectorClassName
	 */
	private ChannelSelectorType(String channelSelectorClassName) {
		// TODO Auto-generated constructor stub
		this.channelSelectorClassName = channelSelectorClassName;
	}

	public String getChannelSelectorClassName() {
		return channelSelectorClassName;
	}
}

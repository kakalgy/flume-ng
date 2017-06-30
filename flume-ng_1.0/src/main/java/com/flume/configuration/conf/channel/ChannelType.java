package com.flume.configuration.conf.channel;

/**
 * Enumeration of built in channel types available in the system.
 * <p/>
 * channel的类型
 * 
 * @author
 *
 */
public enum ChannelType {
	/**
	 * Place holder for custom channels not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * File channel
	 * 
	 * @see FileChannel
	 */
	FILE("org.apache.flume.channel.file.FileChannel"),

	/**
	 * Memory channel
	 *
	 * @see MemoryChannel
	 */
	MEMORY("org.apache.flume.channel.MemoryChannel"),

	/**
	 * JDBC channel provided by org.apache.flume.channel.jdbc.JdbcChannel
	 */
	JDBC("org.apache.flume.channel.jdbc.JdbcChannel"),

	/**
	 * Spillable Memory channel
	 *
	 * @see SpillableMemoryChannel
	 */
	SPILLABLEMEMORY("org.apache.flume.channel.SpillableMemoryChannel");

	private final String channelClassName;

	/**
	 * 构造函数
	 * 
	 * @param channelClassName
	 */
	private ChannelType(String channelClassName) {
		// TODO Auto-generated constructor stub
		this.channelClassName = channelClassName;
	}

	public String getChannelClassName() {
		return channelClassName;
	}
}

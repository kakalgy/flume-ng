package com.flume.node;

import com.flume.core.Channel;
import com.flume.core.SinkRunner;
import com.flume.core.SourceRunner;
import com.google.common.collect.ImmutableMap;

/**
 * MaterializedConfiguration represents the materialization(具体化、物质化) of a Flume
 * properties file. That is it's the actual Source, Sink, and Channels
 * represented in the configuration file.
 * 
 * @author
 *
 */
public interface MaterializedConfiguration {

	// 未完成
	public void addSourceRunner(String name, SourceRunner sourceRunner);

	public void addSinkRunner(String name, SinkRunner sinkRunner);

	public void addChannel(String name, Channel channel);

	public ImmutableMap<String, SourceRunner> getSourceRunners();

	public ImmutableMap<String, SinkRunner> getSinkRunners();

	public ImmutableMap<String, Channel> getChannels();
}

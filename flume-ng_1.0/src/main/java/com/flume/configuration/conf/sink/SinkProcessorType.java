package com.flume.configuration.conf.sink;

public enum SinkProcessorType {
	/**
	 * Place holder for custom sinks not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * Failover processor 故障转移实现：
	 *
	 * @see org.apache.flume.sink.FailoverSinkProcessor
	 */
	FAILOVER("org.apache.flume.sink.FailoverSinkProcessor"),

	/**
	 * Standard processor DefaultSinkProcessor：默认实现，用于单个Sink的场景使用。
	 *
	 * @see org.apache.flume.sink.DefaultSinkProcessor
	 */
	DEFAULT("org.apache.flume.sink.DefaultSinkProcessor"),

	/**
	 * Load balancing processor
	 * 用于实现Sink的负载均衡，其通过SinkSelector进行实现，类似于ChannelSelector。
	 * LoadBalanceSinkProcessor在启动时会根据配置，如agent.sinkgroups.g1.processor.selector=random进行选择，默认提供了两种选择器：
	 * RoundRobinOrderSelector,RandomOrderSinkSelector
	 * 
	 * @see org.apache.flume.sink.LoadBalancingSinkProcessor
	 */
	LOAD_BALANCE("org.apache.flume.sink.LoadBalancingSinkProcessor");

	private final String processorClassName;

	private SinkProcessorType(String processorClassName) {
		// TODO Auto-generated constructor stub
		this.processorClassName = processorClassName;
	}

	public String getProcessorClassName() {
		return processorClassName;
	}
}

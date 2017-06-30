package com.flume.configuration.conf.sink;

public enum SinkProcessorType {
	/**
	 * Place holder for custom sinks not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * Failover processor
	 *
	 * @see org.apache.flume.sink.FailoverSinkProcessor
	 */
	FAILOVER("org.apache.flume.sink.FailoverSinkProcessor"),

	/**
	 * Standard processor
	 *
	 * @see org.apache.flume.sink.DefaultSinkProcessor
	 */
	DEFAULT("org.apache.flume.sink.DefaultSinkProcessor"),

	/**
	 * Load balancing processor
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

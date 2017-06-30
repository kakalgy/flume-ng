package com.flume.configuration.conf.source;

public enum SourceType {
	/**
	 * Place holder for custom sources not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * Sequence generator file source.
	 *
	 * @see org.apache.flume.source.SequenceGeneratorSource
	 */
	SEQ("org.apache.flume.source.SequenceGeneratorSource"),

	/**
	 * Netcat source.
	 *
	 * @see org.apache.flume.source.NetcatSource
	 */
	NETCAT("org.apache.flume.source.NetcatSource"),

	/**
	 * Exec source.
	 *
	 * @see org.apache.flume.source.ExecSource
	 */
	EXEC("org.apache.flume.source.ExecSource"),

	/**
	 * Avro source.
	 *
	 * @see org.apache.flume.source.AvroSource
	 */
	AVRO("org.apache.flume.source.AvroSource"),

	/**
	 * SyslogTcpSource
	 *
	 * @see org.apache.flume.source.SyslogTcpSource
	 */
	SYSLOGTCP("org.apache.flume.source.SyslogTcpSource"),

	/**
	 * MultiportSyslogTCPSource
	 *
	 * @see org.apache.flume.source.MultiportSyslogTCPSource
	 */
	MULTIPORT_SYSLOGTCP("org.apache.flume.source.MultiportSyslogTCPSource"),

	/**
	 * SyslogUDPSource
	 *
	 * @see org.apache.flume.source.SyslogUDPSource
	 */
	SYSLOGUDP("org.apache.flume.source.SyslogUDPSource"),

	/**
	 * Spool directory source
	 *
	 * @see org.apache.flume.source.SpoolDirectorySource
	 */
	SPOOLDIR("org.apache.flume.source.SpoolDirectorySource"),

	/**
	 * HTTP Source
	 *
	 * @see org.apache.flume.source.http.HTTPSource
	 */
	HTTP("org.apache.flume.source.http.HTTPSource"),

	/**
	 * Thrift Source
	 *
	 * @see org.apache.flume.source.ThriftSource
	 */
	THRIFT("org.apache.flume.source.ThriftSource"),

	/**
	 * JMS Source
	 *
	 * @see org.apache.flume.source.jms.JMSSource
	 */
	JMS("org.apache.flume.source.jms.JMSSource"),

	/**
	 * Taildir Source
	 *
	 * @see org.apache.flume.source.taildir.TaildirSource
	 */
	TAILDIR("org.apache.flume.source.taildir.TaildirSource");

	private final String sourceClassName;

	private SourceType(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}

	public String getSourceClassName() {
		return sourceClassName;
	}
}

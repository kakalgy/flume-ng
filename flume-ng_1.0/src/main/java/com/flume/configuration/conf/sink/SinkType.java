package com.flume.configuration.conf.sink;

/**
 * Enumeration of built in sink types available in the system.
 */
public enum SinkType {

	/**
	 * Place holder for custom sinks not part of this enumeration.
	 */
	OTHER(null),

	/**
	 * Null sink
	 *
	 * @see NullSink
	 */
	NULL("org.apache.flume.sink.NullSink"),

	/**
	 * Logger sink
	 *
	 * @see LoggerSink
	 */
	LOGGER("org.apache.flume.sink.LoggerSink"),

	/**
	 * Rolling file sink
	 *
	 * @see RollingFileSink
	 */
	FILE_ROLL("org.apache.flume.sink.RollingFileSink"),

	/**
	 * HDFS Sink provided by org.apache.flume.sink.hdfs.HDFSEventSink
	 */
	HDFS("org.apache.flume.sink.hdfs.HDFSEventSink"),

	/**
	 * IRC Sink provided by org.apache.flume.sink.irc.IRCSink
	 */
	IRC("org.apache.flume.sink.irc.IRCSink"),

	/**
	 * Avro sink
	 *
	 * @see AvroSink
	 */
	AVRO("org.apache.flume.sink.AvroSink"),

	/**
	 * Thrift sink
	 *
	 * @see ThriftSink
	 */
	THRIFT("org.apache.flume.sink.ThriftSink"),

	/**
	 * ElasticSearch sink
	 *
	 * @see org.apache.flume.sink.elasticsearch.ElasticSearchSink
	 */
	ELASTICSEARCH("org.apache.flume.sink.elasticsearch.ElasticSearchSink"),

	/**
	 * HBase sink
	 *
	 * @see org.apache.flume.sink.hbase.HBaseSink
	 */
	HBASE("org.apache.flume.sink.hbase.HBaseSink"),

	/**
	 * AsyncHBase sink
	 *
	 * @see org.apache.flume.sink.hbase.AsyncHBaseSink
	 */
	ASYNCHBASE("org.apache.flume.sink.hbase.AsyncHBaseSink"),

	/**
	 * MorphlineSolr sink
	 *
	 * @see org.apache.flume.sink.solr.morphline.MorphlineSolrSink
	 */
	MORPHLINE_SOLR("org.apache.flume.sink.solr.morphline.MorphlineSolrSink"),

	/**
	 * Hive Sink
	 * 
	 * @see org.apache.flume.sink.hive.HiveSink
	 */
	HIVE("org.apache.flume.sink.hive.HiveSink");

	private final String sinkClassName;

	private SinkType(String sinkClassName) {
		// TODO Auto-generated constructor stub
		this.sinkClassName = sinkClassName;
	}

	public String getSinkClassName() {
		return sinkClassName;
	}
}

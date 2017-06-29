package com.flume.configuration.conf.sink;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ComponentConfiguration;
import com.flume.configuration.conf.ConfigurationException;
import com.flume.configuration.conf.FlumeConfiguration;
import com.flume.configuration.conf.FlumeConfigurationError;
import com.flume.configuration.conf.FlumeConfigurationError.ErrorOrWarning;
import com.flume.configuration.conf.FlumeConfigurationErrorType;

public class SinkConfiguration extends ComponentConfiguration {

	protected String channel;

	/**
	 * 构造函数
	 * 
	 * @param componentName
	 */
	public SinkConfiguration(String componentName) {
		// TODO Auto-generated constructor stub
		super(componentName);
	}

	@Override
	public void configure(Context context) throws ConfigurationException {
		// TODO Auto-generated method stub
		super.configure(context);
		this.channel = context.getString("channel");
		if (this.channel == null || this.channel.isEmpty()) {
			errors.add(new FlumeConfigurationError(componentName, "channel", FlumeConfigurationErrorType.PROPERTY_VALUE_NULL,
					ErrorOrWarning.ERROR));
			throw new ConfigurationException("No channel configured for sink: " + this.getComponentName());
		}
	}

	/**
	 * 添加缩进符
	 */
	@Override
	public String toString(int indentCount) {
		// TODO Auto-generated method stub
		StringBuilder intendSb = new StringBuilder("");

		for (int i = 0; i < indentCount; i++) {
			intendSb.append(FlumeConfiguration.INDENTSTEP);
		}

		String basicStr = super.toString(indentCount);
		StringBuilder sb = new StringBuilder();
		sb.append(basicStr).append(FlumeConfiguration.INDENTSTEP).append("CHANNEL:").append(this.channel)
				.append(FlumeConfiguration.NEWLINE);
		return sb.toString();
	}

	/**
	 * 枚举，Sink配置的种类
	 * 
	 * @author Administrator
	 *
	 */
	public enum SinkConfigurationType {
		/**
		 * Place holder for custom sinks not part of this enumeration.
		 */
		OTHER(null),

		/**
		 * Null sink
		 *
		 * @see NullSink
		 */
		NULL("org.apache.flume.conf.sink.NullSinkConfiguration"),

		/**
		 * Logger sink
		 *
		 * @see LoggerSink
		 */
		LOGGER(null),

		/**
		 * Rolling file sink
		 *
		 * @see RollingFileSink
		 */
		FILE_ROLL("org.apache.flume.conf.sink.RollingFileSinkConfiguration"),

		/**
		 * HDFS Sink provided by org.apache.flume.sink.hdfs.HDFSEventSink
		 */
		HDFS("org.apache.flume.conf.sink.HDFSSinkConfiguration"),

		/**
		 * IRC Sink provided by org.apache.flume.sink.irc.IRCSink
		 */
		IRC("org.apache.flume.conf.sink.IRCSinkConfiguration"),

		/**
		 * Avro sink
		 *
		 * @see AvroSink
		 */
		AVRO("org.apache.flume.conf.sink.AvroSinkConfiguration"),

		/**
		 * Thrift sink
		 *
		 * @see ThriftSink
		 */
		THRIFT("org.apache.flume.conf.sink.ThriftSinkConfiguration"),

		/**
		 * ElasticSearch Sink
		 *
		 * @see org.apache.flume.sink.elasticsearch.ElasticSearchSink
		 */
		ELASTICSEARCH("org.apache.flume.sink.elasticsearch.ElasticSearchSinkConfiguration"),

		/**
		 * HBase Sink
		 *
		 * @see org.apache.flume.sink.hbase.HBaseSink
		 */
		HBASE("org.apache.flume.sink.hbase.HBaseSinkConfiguration"),

		/**
		 * AsyncHBase Sink
		 *
		 * @see org.apache.flume.sink.hbase.AsyncHBaseSink
		 */
		ASYNCHBASE("org.apache.flume.sink.hbase.HBaseSinkConfiguration"),

		/**
		 * MorphlineSolr sink
		 *
		 * @see org.apache.flume.sink.solr.morphline.MorphlineSolrSink
		 */
		MORPHLINE_SOLR("org.apache.flume.sink.solr.morphline" + ".MorphlineSolrSinkConfiguration"),

		/**
		 * Hive Sink
		 * 
		 * @see org.apache.flume.sink.hive.HiveSink
		 */
		HIVE("org.apache.flume.sink.hive.HiveSinkConfiguration");

		private final String sinkConfigurationName;

		/**
		 * 构造函数
		 * 
		 * @param type
		 */
		private SinkConfigurationType(String type) {
			// TODO Auto-generated constructor stub
			this.sinkConfigurationName = type;
		}

		public String getSinkConfigurationName() {
			return this.sinkConfigurationName;
		}

		/**
		 * 根据name的不同(SinkConfiguration的类型)，按照对应的Sink配置类型进行初始化，使用反射调用对应的构造函数，若找不到Type中对应的类型，则初始化为普通的SinkConfiguration类型
		 * 
		 * @param name
		 * @return
		 * @throws ConfigurationException
		 */
		@SuppressWarnings("unchecked")
		public SinkConfiguration getConfiguration(String name) throws ConfigurationException {
			if (this.equals(SinkConfigurationType.OTHER)) {
				return new SinkConfiguration(name);
			}

			Class<? extends SinkConfiguration> clazz;
			SinkConfiguration instance = null;

			try {
				if (sinkConfigurationName != null) {
					clazz = (Class<? extends SinkConfiguration>) Class.forName(sinkConfigurationName);
					instance = clazz.getConstructor(String.class).newInstance(name);
				} else {
					return new SinkConfiguration(name);
				}
			} catch (ClassNotFoundException e) {
				// TODO: handle exception
				// Could not find the configuration stub, do basic validation
				instance = new SinkConfiguration(name);
				// Let the caller know that this was created because of this
				// exception.
				instance.setNotFoundConfigClass();
			} catch (Exception e) {
				// TODO: handle exception
				throw new ConfigurationException("Couldn't create configuration", e);
			}
			return instance;
		}
	}

	/***************************** Get/Set方法 **************************************/
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
}

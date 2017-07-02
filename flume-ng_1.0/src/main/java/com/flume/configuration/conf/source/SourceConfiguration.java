package com.flume.configuration.conf.source;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.flume.configuration.Context;
import com.flume.configuration.conf.BasicConfigurationConstants;
import com.flume.configuration.conf.ComponentConfiguration;
import com.flume.configuration.conf.ComponentConfigurationFactory;
import com.flume.configuration.conf.ConfigurationException;
import com.flume.configuration.conf.FlumeConfiguration;
import com.flume.configuration.conf.FlumeConfigurationError;
import com.flume.configuration.conf.FlumeConfigurationError.ErrorOrWarning;
import com.flume.configuration.conf.FlumeConfigurationErrorType;
import com.flume.configuration.conf.ComponentConfiguration.ComponentType;
import com.flume.configuration.conf.channel.ChannelSelectorConfiguration;
import com.flume.configuration.conf.channel.ChannelSelectorConfiguration.ChannelSelectorConfigurationType;
import com.flume.configuration.conf.channel.ChannelSelectorType;

public class SourceConfiguration extends ComponentConfiguration {

	protected Set<String> channels;
	protected ChannelSelectorConfiguration selectorConf;

	protected SourceConfiguration(String componentName) {
		// TODO Auto-generated constructor stub
		super(componentName);
		this.channels = new HashSet<String>();
	}

	@Override
	public void configure(Context context) throws ConfigurationException {
		// TODO Auto-generated method stub
		super.configure(context);

		try {
			String channelList = context.getString(BasicConfigurationConstants.CONFIG_CHANNELS);
			if (channelList != null) {
				this.channels = new HashSet<String>(Arrays.asList(channelList.split("\\s+")));
			}
			if (channels.isEmpty()) {
				this.errors.add(new FlumeConfigurationError(componentName, ComponentType.CHANNEL.getComponentType(),
						FlumeConfigurationErrorType.PROPERTY_VALUE_NULL, ErrorOrWarning.ERROR));
				throw new ConfigurationException("No channels set for " + this.getComponentName());
			}

			Map<String, String> selectorParams = context.getSubProperties(BasicConfigurationConstants.CONFIG_SOURCE_CHANNELSELECTOR_PREFIX);
			String selType;

			if (selectorParams != null && !selectorParams.isEmpty()) {
				selType = selectorParams.get(BasicConfigurationConstants.CONFIG_TYPE);
			} else {
				selType = ChannelSelectorConfigurationType.REPLICATING.toString();
			}

			if (selType == null || selType.isEmpty()) {
				selType = ChannelSelectorConfigurationType.REPLICATING.toString();
			}

			ChannelSelectorType selectorType = this.getKnownChannelSelector(selType);
			Context selectorContext = new Context();
			selectorContext.putAll(selectorParams);
			String config = null;

			if (selectorType == null) {
				config = selectorContext.getString(BasicConfigurationConstants.CONFIG_CONFIG);
				if (config == null || config.isEmpty()) {
					config = "OTHER";
				}
			} else {
				config = selectorType.toString().toUpperCase(Locale.ENGLISH);
			}

			this.selectorConf = (ChannelSelectorConfiguration) ComponentConfigurationFactory
					.create(ComponentType.CHANNELSELECTOR.getComponentType(), config, ComponentType.CHANNELSELECTOR);
			this.selectorConf.setChannels(channels);
			this.selectorConf.configure(selectorContext);

		} catch (Exception e) {
			errors.add(new FlumeConfigurationError(componentName, ComponentType.CHANNELSELECTOR.getComponentType(),
					FlumeConfigurationErrorType.CONFIG_ERROR, ErrorOrWarning.ERROR));
			throw new ConfigurationException("Failed to configure component!", e);
		}
	}

	@Override
	public String toString(int indentCount) {
		// TODO Auto-generated method stub
		String basicStr = super.toString(indentCount);
		StringBuilder sb = new StringBuilder();
		sb.append(basicStr).append("CHANNELS:");
		for (String channel : this.channels) {
			sb.append(FlumeConfiguration.INDENTSTEP).append(channel).append(FlumeConfiguration.NEWLINE);
		}
		return sb.toString();
	}

	/**
	 * 返回已在ChannelSelectorType中定义的类型，若没有找到与参数对应的，则返回null
	 * 
	 * @param type
	 * @return ChannelSelectorType枚举类型的值 或者值所代表的类全名字符串
	 */
	private ChannelSelectorType getKnownChannelSelector(String type) {
		ChannelSelectorType[] values = ChannelSelectorType.values();

		for (ChannelSelectorType value : values) {
			if (value.toString().equalsIgnoreCase(type)) {
				return value;
			}
			String clName = value.getChannelSelectorClassName();
			if (clName != null && clName.equalsIgnoreCase(type)) {
				return value;
			}
		}
		return null;
	}

	public enum SourceConfigurationType {
		OTHER(null),

		SEQ(null),
		/**
		 * Netcat source.
		 *
		 * @see NetcatSource
		 */
		NETCAT("org.apache.flume.conf.source.NetcatSourceConfiguration"),

		/**
		 * Exec source.
		 *
		 * @see ExecSource
		 */
		EXEC("org.apache.flume.conf.source.ExecSourceConfiguration"),

		/**
		 * Avro source.
		 *
		 * @see AvroSource
		 */
		AVRO("org.apache.flume.conf.source.AvroSourceConfiguration"),

		/**
		 * Syslog Tcp Source
		 *
		 * @see org.apache.flume.source.SyslogTcpSource
		 */
		SYSLOGTCP("org.apache.flume.conf.source.SyslogTcpSourceConfiguration"),

		/**
		 * Syslog Udp Source
		 *
		 * @see org.apache.flume.source.SyslogUDPSource
		 */
		SYSLOGUDP("org.apache.flume.conf.source.SyslogUDPSourceConfiguration"),

		/**
		 * Multiport Syslog TCP Source
		 *
		 * @see org.apache.flume.source.MultiportSyslogTCPSource
		 *
		 */
		MULTIPORT_SYSLOGTCP("org.apache.flume.source.MultiportSyslogTCPSourceConfiguration"),

		/**
		 * Spool directory source
		 *
		 * @see org.apache.flume.source.SpoolDirectorySource
		 */
		SPOOLDIR("org.apache.flume.conf.source.SpoolDirectorySourceConfiguration"),

		/**
		 * HTTP Source
		 *
		 * @see org.apache.flume.source.http.HTTPSource
		 */
		HTTP("org.apache.flume.source.http.HTTPSourceConfiguration"),

		/**
		 * HTTP Source
		 *
		 * @see org.apache.flume.source.ThriftSource
		 */
		THRIFT("org.apache.flume.source.http.ThriftSourceConfiguration"),

		/**
		 * JMS Source
		 *
		 * @see org.apache.flume.source.jms.JMSSource
		 */
		JMS("org.apache.flume.conf.source.jms.JMSSourceConfiguration"),

		/**
		 * TAILDIR Source
		 *
		 * @see org.apache.flume.source.taildir.TaildirSource
		 */
		TAILDIR("org.apache.flume.source.taildir.TaildirSourceConfiguration");

		private String srcConfigurationClassName;

		private SourceConfigurationType(String srcClassName) {
			// TODO Auto-generated constructor stub
			this.srcConfigurationClassName = srcClassName;
		}

		public String getSourceConfigurationType() {
			return this.getSourceConfigurationType();// ???
		}

		@SuppressWarnings("unchecked")
		public SourceConfiguration getConfiguration(String componentName) throws ConfigurationException {
			if (this.equals(SourceConfigurationType.OTHER)) {
				return new SourceConfiguration(componentName);
			}
			Class<? extends SourceConfiguration> clazz = null;
			SourceConfiguration instance = null;
			try {
				if (srcConfigurationClassName != null) {
					clazz = (Class<? extends SourceConfiguration>) Class.forName(srcConfigurationClassName);
					instance = clazz.getConstructor(String.class).newInstance(componentName);
				} else {
					// Could not find the configuration stub, do basic
					// validation
					instance = new SourceConfiguration(componentName);
					// Let the caller know that this was created because of this
					// exception.
					instance.setNotFoundConfigClass();
				}
			} catch (ClassNotFoundException e) {
				// Could not find the configuration stub, do basic validation
				instance = new SourceConfiguration(componentName);
				// Let the caller know that this was created because of this
				// exception.
				instance.setNotFoundConfigClass();
			} catch (Exception e) {
				throw new ConfigurationException("Error creating configuration", e);
			}
			return instance;
		}
	}

	/******************************** Get/Set方法 **************************************/
	public Set<String> getChannels() {
		return channels;
	}

	public ChannelSelectorConfiguration getSelectorConf() {
		return selectorConf;
	}
}

package com.flume.configuration.conf;

import java.util.Locale;

import com.flume.configuration.conf.ComponentConfiguration.ComponentType;
import com.flume.configuration.conf.channel.ChannelConfiguration.ChannelConfigurationType;
import com.flume.configuration.conf.channel.ChannelSelectorConfiguration.ChannelSelectorConfigurationType;
import com.flume.configuration.conf.sink.SinkConfiguration.SinkConfigurationType;
import com.flume.configuration.conf.sink.SinkGroupConfiguration;
import com.flume.configuration.conf.sink.SinkProcessorConfiguration.SinkProcessorConfigurationType;
import com.flume.configuration.conf.source.SourceConfiguration.SourceConfigurationType;

/**
 * 工厂模式 创建新的组件
 * 
 * @author
 *
 */
public class ComponentConfigurationFactory {

	/**
	 * 工厂模式 创建新的组件
	 * 
	 * @param componentName
	 *            组件名称
	 * @param type
	 *            组件配置类的全名
	 * @param componentType
	 *            组件类型
	 * @return
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("unchecked")
	public static ComponentConfiguration create(String componentName, String type, ComponentType componentType)
			throws ConfigurationException {
		Class<? extends ComponentConfiguration> confType = null;

		if (type == null) {
			throw new ConfigurationException("Cannot create component without knowing its type!");
		}

		try {
			confType = (Class<? extends ComponentConfiguration>) Class.forName(type);
			return confType.getConstructor(String.class).newInstance(type);
		} catch (Exception ignored) {
			try {
				type = type.toUpperCase(Locale.ENGLISH);
				switch (componentType) {
				case SOURCE:
					return SourceConfigurationType.valueOf(type.toUpperCase(Locale.ENGLISH)).getConfiguration(componentName);
				case SINK:
					return SinkConfigurationType.valueOf(type.toUpperCase(Locale.ENGLISH)).getConfiguration(componentName);
				case CHANNEL:
					return ChannelConfigurationType.valueOf(type.toUpperCase(Locale.ENGLISH)).getConfiguration(componentName);
				case SINK_PROCESSOR:
					return SinkProcessorConfigurationType.valueOf(type.toUpperCase(Locale.ENGLISH)).getConfiguration(componentName);
				case CHANNELSELECTOR:
					return ChannelSelectorConfigurationType.valueOf(type.toUpperCase(Locale.ENGLISH)).getConfiguration(componentName);
				case SINKGROUP:
					return new SinkGroupConfiguration(componentName);
				default:
					throw new ConfigurationException("Cannot create configuration. Unknown Type specified: " + type);
				}
			} catch (ConfigurationException e) {
				throw e;
			} catch (Exception e) {
				throw new ConfigurationException(
						"Could not create configuration! " + " Due to " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
			}
		}
	}
}

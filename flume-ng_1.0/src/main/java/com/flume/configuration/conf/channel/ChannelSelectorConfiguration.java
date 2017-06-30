package com.flume.configuration.conf.channel;

import java.util.Set;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ComponentConfiguration;
import com.flume.configuration.conf.ConfigurationException;
import com.flume.configuration.conf.channel.ChannelConfiguration.ChannelConfigurationType;

/**
 * 
 * @author
 *
 */
public class ChannelSelectorConfiguration extends ComponentConfiguration {

	/**
	 * channel的集合
	 */
	protected Set<String> channelNames;

	/**
	 * 构造函数
	 * 
	 * @param componentName
	 */
	protected ChannelSelectorConfiguration(String componentName) {
		// TODO Auto-generated constructor stub
		super(componentName);
		// unless it is set to some other type
		this.setType(ChannelSelectorType.REPLICATING.toString());
		channelNames = null;
	}

	@Override
	public void configure(Context context) throws ConfigurationException {
		// TODO Auto-generated method stub
		super.configure(context);
	}

	public enum ChannelSelectorConfigurationType {
		OTHER(null), REPLICATING(null), MULTIPLEXING("org.apache.flume.conf.channel." + "MultiplexingChannelSelectorConfiguration");

		private String selectorType;

		private ChannelSelectorConfigurationType(String type) {
			// TODO Auto-generated constructor stub
			this.selectorType = type;
		}

		public String getSelectorType() {
			return selectorType;
		}

		@SuppressWarnings("unchecked")
		public ChannelSelectorConfiguration getConfiguration(String name) throws ConfigurationException {

			if (this.equals(ChannelConfigurationType.OTHER)) {
				return new ChannelSelectorConfiguration(name);
			}

			Class<? extends ChannelSelectorConfiguration> clazz;
			ChannelSelectorConfiguration instance = null;

			try {
				// Components where it is null, no configuration is necessary.
				if (this.selectorType != null) {
					clazz = (Class<? extends ChannelSelectorConfiguration>) Class.forName(this.selectorType);
					instance = clazz.getConstructor(String.class).newInstance(name);
				} else {
					return new ChannelSelectorConfiguration(name);
				}
			} catch (ClassNotFoundException e) {
				// Could not find the configuration stub, do basic validation
				instance = new ChannelSelectorConfiguration(name);
				// Let the caller know that this was created because of this
				// exception.
				instance.setNotFoundConfigClass();
			} catch (Exception e) {
				throw new ConfigurationException("Configuration error!", e);

			}
			return instance;
		}

	}

	/****************************** Get/Set方法 ************************************/
	public Set<String> getChannels() {
		return this.channelNames;
	}

	public void setChannels(Set<String> channelNames) {
		this.channelNames = channelNames;
	}
}

package com.flume.configuration.conf.channel;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ComponentConfiguration;
import com.flume.configuration.conf.ConfigurationException;

public class ChannelConfiguration extends ComponentConfiguration {

	public ChannelConfiguration(String componentName) {
		// TODO Auto-generated constructor stub
		super(componentName);
	}

	@Override
	public void configure(Context context) throws ConfigurationException {
		// TODO Auto-generated method stub
		super.configure(context);
	}

	public enum ChannelConfigurationType {
		OTHER(null), MEMORY("org.apache.flume.conf.channel.MemoryChannelConfiguration"),

		/**
		 * File channel
		 */
		FILE("org.apache.flume.conf.channel.FileChannelConfiguration"),

		/**
		 * JDBC channel provided by org.apache.flume.channel.jdbc.JdbcChannel
		 */
		JDBC("org.apache.flume.conf.channel.JdbcChannelConfiguration"),

		/**
		 * Spillable Memory channel
		 */
		SPILLABLEMEMORY("org.apache.flume.conf.channel.SpillableMemoryChannelConfiguration");

		private String channelConfigurationType;

		/**
		 * 构造函数
		 * 
		 * @param type
		 */
		private ChannelConfigurationType(String type) {
			// TODO Auto-generated constructor stub
			this.channelConfigurationType = type;
		}

		public String getChannelConfigurationType() {
			return this.channelConfigurationType;
		}

		/**
		 * 
		 * @param name
		 *            组件的名称 这里是指Channel的名称 而不是Channel的类型
		 * @return
		 * @throws ConfigurationException
		 */
		@SuppressWarnings("unchecked")
		public ChannelConfiguration getConfiguration(String name) throws ConfigurationException {
			if (this.equals(ChannelConfigurationType.OTHER)) {
				return new ChannelConfiguration(name);
			}
			Class<? extends ChannelConfiguration> clazz;
			ChannelConfiguration instance = null;

			try {
				if (channelConfigurationType != null) {
					clazz = (Class<? extends ChannelConfiguration>) Class.forName(channelConfigurationType);
					instance = clazz.getConstructor(String.class).newInstance(name);
				} else {
					return new ChannelConfiguration(name);
				}
			} catch (ClassNotFoundException e) {
				// TODO: handle exception
				// Could not find the configuration stub, do basic validation
				instance = new ChannelConfiguration(name);
				// Let the caller know that this was created because of this
				// exception.
				instance.setNotFoundConfigClass();
			} catch (Exception e) {
				// TODO: handle exception
				throw new ConfigurationException(e);
			}
			return instance;
		}

	}
}

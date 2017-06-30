package com.flume.configuration.conf.sink;

import java.util.Set;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ComponentConfiguration;
import com.flume.configuration.conf.ConfigurationException;

public class SinkProcessorConfiguration extends ComponentConfiguration {

	protected Set<String> sinks;

	/**
	 * 构造函数
	 * 
	 * @param componentName
	 */
	protected SinkProcessorConfiguration(String componentName) {
		super(componentName);
		this.setType("default");
	}

	@Override
	public void configure(Context context) throws ConfigurationException {
		// TODO Auto-generated method stub

	}

	public enum SinkProcessorConfigurationType {
		/**
		 * Load balancing channel selector
		 */
		LOAD_BALANCE("org.apache.flume.conf.sink.LoadBalancingSinkProcessorConfiguration"),
		/**
		 * Failover processor
		 *
		 * @see FailoverSinkProcessor
		 */
		FAILOVER("org.apache.flume.conf.sink.FailoverSinkProcessorConfiguration"),

		/**
		 * Standard processor
		 *
		 * @see DefaultSinkProcessor
		 */
		DEFAULT(null);
		private final String processorClassName;

		private SinkProcessorConfigurationType(String processorClassName) {
			// TODO Auto-generated constructor stub
			this.processorClassName = processorClassName;
		}

		public String getProcessorClassName() {
			return processorClassName;
		}

		@SuppressWarnings("unchecked")
		public SinkProcessorConfiguration getConfiguration(String componentName) throws ConfigurationException {
			Class<? extends SinkProcessorConfiguration> clazz;
			SinkProcessorConfiguration instance = null;

			try {
				if (this.processorClassName != null) {
					clazz = (Class<? extends SinkProcessorConfiguration>) Class.forName(processorClassName);
					instance = clazz.getConstructor(String.class).newInstance(componentName);
				} else {
					return new SinkProcessorConfiguration(componentName);
				}
			} catch (ClassNotFoundException e) {
				// Could not find the configuration stub, do basic validation
				instance = new SinkProcessorConfiguration(componentName);
				// Let the caller know that this was created because of this
				// exception.
				instance.setNotFoundConfigClass();
			} catch (Exception e) {
				throw new ConfigurationException("Could not instantiate configuration!", e);
			}
			return instance;
		}
	}

	/******************************** Get/Set方法 **************************************/
	public Set<String> getSinks() {
		return sinks;
	}

	public void setSinks(Set<String> sinks) {
		this.sinks = sinks;
	}
}

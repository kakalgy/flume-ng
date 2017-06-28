package com.flume.configuration.conf;

import java.util.List;

/**
 *
 * Abstract implementation of the Component Configuration Manager. This class
 * does the configuration in the object. The component specific versions, which
 * inherit from this, create the configuration based on the config file. All
 * subclasses of this class store properties of the component. The properties
 * can be stored as properties in this
 *
 */
public abstract class ComponentConfiguration {

	protected String componentName;

	private String type;
	protected boolean configured;
	protected List<FlumeConfigurarionError> errors;
	private boolean notFoundConfigClass;

	/**
	 * 枚举，组件类型
	 * 
	 * @author
	 *
	 */
	public enum ComponentType {
		OTHER(null), SOURCE("Source"), SINK("Sink"), SINK_PROCESSOR("SinkProcessor"), SINKGROUP("Sinkgroup"), CHANNEL("Channel"), CHANNELSELECTOR(
				"ChannelSelector");

		private final String componentType;

		/**
		 * 构造函数
		 */
		private ComponentType(String type) {
			// TODO Auto-generated constructor stub
			this.componentType = type;
		}

		public String getComponentType() {
			return componentType;
		}
	}
}

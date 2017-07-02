package com.flume.configuration.conf;

import java.util.LinkedList;
import java.util.List;

import com.flume.configuration.Context;
import com.flume.configuration.conf.FlumeConfigurationError.ErrorOrWarning;

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
	protected List<FlumeConfigurationError> errors;
	private boolean notFoundConfigClass;

	protected ComponentConfiguration(String componentName) {
		// TODO Auto-generated constructor stub
		this.componentName = componentName;
		errors = new LinkedList<FlumeConfigurationError>();
		this.type = null;
		this.configured = false;
	}

	public void configure(Context context) throws ConfigurationException {
		failIfConfigured();

		String confType = context.getString(BasicConfigurationConstants.CONFIG_TYPE);

		if (confType != null && !confType.isEmpty()) {
			this.type = confType;
		}
		// Type can be set by child class constructors, so check if it was.
		if (this.type == null || this.type.isEmpty()) {
			errors.add(new FlumeConfigurationError(componentName, BasicConfigurationConstants.CONFIG_TYPE,
					FlumeConfigurationErrorType.ATTRS_MISSING, ErrorOrWarning.ERROR));

			throw new ConfigurationException("Component has no type. Cannot configure. " + componentName);
		}
	}

	/**
	 * 当配置Component时发现configured属性为true，则认为已配置过，抛出异常
	 * 
	 * @throws ConfigurationException
	 */
	protected void failIfConfigured() throws ConfigurationException {
		if (this.configured) {
			throw new ConfigurationException("Already configured component." + componentName);
		}
	}

	/**
	 * 枚举，组件类型
	 * 
	 * @author
	 *
	 */
	public enum ComponentType {
		OTHER(null), SOURCE("Source"), SINK("Sink"), SINK_PROCESSOR("SinkProcessor"), SINKGROUP("Sinkgroup"), CHANNEL(
				"Channel"), CHANNELSELECTOR("ChannelSelector");

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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.toString(0);
	}

	/**
	 * ComponentConfiguration的字符串格式
	 * 
	 * @param indentCount
	 *            字符串每行的缩进位数
	 * @return
	 */
	public String toString(int indentCount) {
		StringBuilder indentSb = new StringBuilder("");

		for (int i = 0; i < indentCount; i++) {
			indentSb.append(FlumeConfiguration.INDENTSTEP);
		}

		String indent = indentSb.toString();
		StringBuilder sb = new StringBuilder(indent);

		sb.append("ComponentConfiguration[").append(componentName).append("]");
		sb.append(FlumeConfiguration.NEWLINE).append(indent).append(FlumeConfiguration.INDENTSTEP).append("CONFIG: ");
		sb.append(FlumeConfiguration.NEWLINE).append(indent).append(FlumeConfiguration.INDENTSTEP);

		return sb.toString();
	}

	/********************* Get/Set方法 **************************/
	public boolean isNotFoundConfigClass() {
		return notFoundConfigClass;
	}

	public void setNotFoundConfigClass() {
		this.notFoundConfigClass = true;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FlumeConfigurationError> getErrors() {
		return errors;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setConfigured() {
		this.configured = true;
	}
}

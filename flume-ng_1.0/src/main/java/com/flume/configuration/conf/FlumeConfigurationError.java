package com.flume.configuration.conf;

public class FlumeConfigurationError {

	private String componentName;
	private String key;
	private final FlumeConfigurationErrorType errorType;
	private ErrorOrWarning error;// 内部类，枚举类型

	/**
	 * 构造函数
	 * <p>
	 * Component which had an error, specific key in error(which can be null)
	 * </p>
	 */
	public FlumeConfigurationError(String componentName, String key, FlumeConfigurationErrorType errorType, ErrorOrWarning error) {
		// TODO Auto-generated constructor stub
		this.error = error;
		if (componentName != null) {
			this.componentName = componentName;
		} else {
			this.componentName = "";
		}
		if (key != null) {
			this.key = key;
		} else {
			this.key = "";
		}
		this.errorType = errorType;
	}

	/**
	 * 枚举，错误或者告警
	 * 
	 * @author
	 *
	 */
	public enum ErrorOrWarning {
		ERROR, WARNING;
	}

	/********************* Get方法 *****************************/
	public String getComponentName() {
		return componentName;
	}

	public String getKey() {
		return key;
	}

	public FlumeConfigurationErrorType getErrorType() {
		return errorType;
	}

	public ErrorOrWarning getError() {
		return error;
	}

}

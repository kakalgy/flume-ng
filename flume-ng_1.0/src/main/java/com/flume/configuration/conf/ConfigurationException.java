package com.flume.configuration.conf;

import com.flume.sdk.FlumeException;

public class ConfigurationException extends FlumeException {

	/**
	   *
	   */
	private static final long serialVersionUID = 1L;

	public ConfigurationException(String arg0) {
		super(arg0);
	}

	public ConfigurationException(Throwable arg0) {
		super(arg0);
	}

	public ConfigurationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}

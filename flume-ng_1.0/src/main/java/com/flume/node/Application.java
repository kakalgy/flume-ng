package com.flume.node;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.core.lifecycle.LifecycleAware;
import com.flume.core.lifecycle.LifecycleSupervisor;

public class Application {

	/**
	 * 定义日志组件
	 */
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private static final String CONF_MONITOR_CLASS = "flume.monitoring.type";
	private static final String CONF_MONITOR_PREFIX = "flume.monitoring.";
	
	private final List<LifecycleAware> components;
	private final LifecycleSupervisor supervisor;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

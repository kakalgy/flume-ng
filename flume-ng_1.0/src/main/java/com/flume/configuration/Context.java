package com.flume.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * The context is a key-value store used to pass configuration information
 * throughout the system.
 * <p>
 * 通过key-value的形式存储和传递系统的的配置信息
 * </p>
 */
public class Context {

	private Map<String, String> parameters;

	/**
	 * 构造函数
	 */
	public Context() {
		// TODO Auto-generated constructor stub
		parameters = Collections.synchronizedMap(new HashMap<String, String>());
	}

	/**
	 * 构造函数
	 * 
	 * @param parameter
	 */
	public Context(Map<String, String> parameter) {
		this();
		this.putAll(parameter);
	}

	/**
	 * Gets a copy of the backing map structure.
	 * 
	 * @return immutable copy of backing map structure
	 */
	public ImmutableMap<String, String> getParameters() {
		synchronized (parameters) {
			return ImmutableMap.copyOf(parameters);
		}
	}

}

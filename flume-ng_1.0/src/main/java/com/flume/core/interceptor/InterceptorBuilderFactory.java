package com.flume.core.interceptor;

import java.util.Locale;

import com.flume.core.interceptor.Interceptor.Builder;

/**
 * Factory used to register instances of Interceptors & their builders, as well
 * as to instantiate the builders.
 */
public class InterceptorBuilderFactory {

	/**
	 * 返回对应interceptorTypeName名字的拦截器类，若异常则返回null
	 * 
	 * @param interceptorTypeName
	 * @return
	 */
	private static Class<? extends Builder> lookup(String interceptorTypeName) {
		try {
			return InterceptorType.valueOf(interceptorTypeName.toUpperCase(Locale.ENGLISH)).getBuilderClass();
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 返回对应interceptorTypeName名字的拦截器类，若InterceptorType中未配置，则根据参数直接生成
	 * 
	 * @param interceptorTypeName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static Builder newInstance(String interceptorTypeName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		Class<? extends Builder> clazz = lookup(interceptorTypeName);
		if (clazz == null) {
			clazz = (Class<? extends Builder>) Class.forName(interceptorTypeName);
		}
		return clazz.newInstance();
	}

}

package com.flume.core.conf;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ComponentConfiguration;

/**
 * Methods for working with {@link Configurable}s.
 */
public class Configurables {

	/**
	 * Check that {@code target} implements {@link Configurable} and, if so, ask
	 * it to configure itself using the supplied {@code context}.
	 *
	 * @param target
	 *            An object that potentially implements Configurable.
	 * @param context
	 *            The configuration context
	 * @return true if {@code target} implements Configurable, false otherwise.
	 */
	public static boolean configure(Object target, Context context) {
		if (target instanceof Configurable) {
			((Configurable) target).configure(context);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param target
	 * @param conf
	 * @return
	 */
	public static boolean configure(Object target, ComponentConfiguration conf) {
		if (target instanceof ConfigurationComponent) {
			((ConfigurationComponent) target).configure(conf);
			return true;
		}
		return false;
	}

	/**
	 * 确认配置信息context中是否包含对应keys的键，且键对应的值不能为空
	 * 
	 * @param context
	 * @param keys
	 *            传入多个key
	 */
	public static void ensureRequiredNonNull(Context context, String... keys) {
		for (String key : keys) {
			if (!context.getParameters().containsKey(key) || context.getParameters().get(key) == null) {
				throw new IllegalArgumentException("Required parameter " + key + " must exist and may not be null");
			}
		}
	}

	/**
	 * 确认配置信息context中是否含有对应keys的键，可不包含这个key，但是如果包含的话，key对应的值必须为null，否则抛出异常
	 * 
	 * @param context
	 * @param keys
	 */
	public static void ensureOptionalNonNull(Context context, String... keys) {
		for (String key : keys) {
			if (context.getParameters().containsKey(key) && context.getParameters().get(key) == null) {
				throw new IllegalArgumentException("Optional parameter " + key + " may not be null");
			}
		}
	}
}

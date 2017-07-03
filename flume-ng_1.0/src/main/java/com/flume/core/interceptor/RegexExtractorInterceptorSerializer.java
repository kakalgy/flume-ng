package com.flume.core.interceptor;

import com.flume.core.conf.Configurable;
import com.flume.core.conf.ConfigurationComponent;

/**
 * Serializer for serializing groups matched by the
 * {@link RegexExtractorInterceptor}
 * <p>
 * 通过RegexExtractorInterceptor匹配的串行器序列化组
 * </p>
 */
public interface RegexExtractorInterceptorSerializer extends Configurable, ConfigurationComponent {
	/**
	 * @param value
	 *            The value extracted by the {@link RegexExtractorInterceptor}
	 * @return The serialized version of the specified value
	 */
	public String serialize(String value);
}

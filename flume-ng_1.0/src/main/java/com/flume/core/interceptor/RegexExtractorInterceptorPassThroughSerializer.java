package com.flume.core.interceptor;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ComponentConfiguration;

/**
 * Serializer that simply returns the passed in value
 * <p>
 * 默认的Serializer串行器,没有操作,直接通过
 * </p>
 * 
 * @author
 *
 */
public class RegexExtractorInterceptorPassThroughSerializer implements RegexExtractorInterceptorSerializer {

	@Override
	public void configure(ComponentConfiguration conf) {
		// TODO Auto-generated method stub
		// 无操作
	}

	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub
		// 无操作
	}

	@Override
	public String serialize(String value) {
		// TODO Auto-generated method stub
		return value;
	}
}

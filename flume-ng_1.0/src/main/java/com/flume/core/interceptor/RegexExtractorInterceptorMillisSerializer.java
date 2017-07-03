package com.flume.core.interceptor;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ComponentConfiguration;
import com.google.common.base.Preconditions;

/**
 * Serializer that converts the passed in value into milliseconds using the
 * specified formatting pattern
 * </p>
 * 通过配置好的日期格式来计算出毫秒数
 */
public class RegexExtractorInterceptorMillisSerializer implements RegexExtractorInterceptorSerializer {

	private DateTimeFormatter formatter;

	@Override
	public void configure(ComponentConfiguration conf) {
		// TODO Auto-generated method stub
		// 无操作
	}

	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub
		String pattern = context.getString("pattern");
		Preconditions.checkArgument(!StringUtils.isEmpty(pattern), "Must configure with a valid pattern");
		this.formatter = DateTimeFormat.forPattern(pattern);
	}

	@Override
	public String serialize(String value) {
		// TODO Auto-generated method stub
		DateTime dateTime = this.formatter.parseDateTime(value);
		return Long.toString(dateTime.getMillis());
	}
}

package com.flume.core.interceptor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.sdk.Event;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * Interceptor that extracts(抽取) matches using a specified regular expression
 * and appends the matches to the event headers using the specified
 * serializers(串行器)
 * </p>
 * 按照配置的serializer（可以是多个），将抽取出对应正则表达式的值进行处理，将处理的返回值插入到Event的Header中
 * </p>
 * Note that all regular expression matching occurs through Java's built in
 * java.util.regex package
 * </p>
 * . Properties:
 * <p>
 * regex: The regex to use
 * <p>
 * serializers: Specifies the group the serializer will be applied to, and the
 * name of the header that will be added. If no serializer is specified for a
 * group the default {@link RegexExtractorInterceptorPassThroughSerializer} will
 * be used
 * <p>
 * Sample config:
 * <p>
 * agent.sources.r1.channels = c1
 * <p>
 * agent.sources.r1.type = SEQ
 * <p>
 * agent.sources.r1.interceptors = i1
 * <p>
 * agent.sources.r1.interceptors.i1.type = REGEX_EXTRACTOR
 * <p>
 * agent.sources.r1.interceptors.i1.regex = (WARNING)|(ERROR)|(FATAL)
 * <p>
 * agent.sources.r1.interceptors.i1.serializers = s1 s2
 * <p>
 * agent.sources.r1.interceptors.i1.serializers.s1.type =
 * com.blah.SomeSerializer
 * <p>
 * agent.sources.r1.interceptors.i1.serializers.s1.name = warning
 * <p>
 * agent.sources.r1.interceptors.i1.serializers.s2.type =
 * org.apache.flume.interceptor.RegexExtractorInterceptorTimestampSerializer
 * <p>
 * agent.sources.r1.interceptors.i1.serializers.s2.name = error
 * <p>
 * agent.sources.r1.interceptors.i1.serializers.s2.dateFormat = yyyy-MM-dd
 * </p>
 * 
 * <pre>
 * Example 1:
 * </p>
 * EventBody: 1:2:3.4foobar5</p> Configuration:
 * agent.sources.r1.interceptors.i1.regex = (\\d):(\\d):(\\d)
 * </p>
 * agent.sources.r1.interceptors.i1.serializers = s1 s2 s3
 * agent.sources.r1.interceptors.i1.serializers.s1.name = one
 * agent.sources.r1.interceptors.i1.serializers.s2.name = two
 * agent.sources.r1.interceptors.i1.serializers.s3.name = three
 * </p>
 * results in an event with the the following
 *
 * body: 1:2:3.4foobar5 headers: one=>1, two=>2, three=3
 *
 * Example 2:
 *
 * EventBody: 1:2:3.4foobar5
 *
 * Configuration: agent.sources.r1.interceptors.i1.regex = (\\d):(\\d):(\\d)
 * <p>
 * agent.sources.r1.interceptors.i1.serializers = s1 s2
 * agent.sources.r1.interceptors.i1.serializers.s1.name = one
 * agent.sources.r1.interceptors.i1.serializers.s2.name = two
 * <p>
 *
 * results in an event with the the following
 *
 * body: 1:2:3.4foobar5 headers: one=>1, two=>2
 * </pre>
 */
public class RegexExtractorInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(RegexExtractorInterceptor.class);

	private static final String REGEX = "regex";
	private static final String SERIALIZERS = "serializers";

	private final Pattern regex;
	private final List<NameAndSerializer> serializers;

	/**
	 * 构造函数
	 * 
	 * @param regex
	 * @param serializers
	 */
	private RegexExtractorInterceptor(Pattern regex, List<NameAndSerializer> serializers) {
		// TODO Auto-generated constructor stub
		this.regex = regex;
		this.serializers = serializers;
	}

	public void initialize() {
		// TODO Auto-generated method stub
		// 无操作
	}

	public Event intercept(Event event) {
		// TODO Auto-generated method stub
		Matcher matcher = this.regex.matcher(new String(event.getBody(), Charsets.UTF_8));
		Map<String, String> headers = event.getHeaders();

		if (matcher.find()) {
			for (int group = 0, count = matcher.groupCount(); group < count; group++) {
				int groupIndex = group + 1;
				if (groupIndex > this.serializers.size()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipping group {} to {} due to missing serializer", group, count);
					}
					break;
				}

				NameAndSerializer serializer = this.serializers.get(group);
				if (logger.isDebugEnabled()) {
					logger.debug("Serializing {} using {}", serializer.headerName, serializer.serializer);
				}

				headers.put(serializer.headerName, serializer.serializer.serialize(matcher.group(groupIndex)));
			}
		}
		return event;
	}

	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());

		for (Event event : events) {
			Event interceptedEvent = this.intercept(event);
			if (interceptedEvent != null) {
				intercepted.add(interceptedEvent);
			}
		}
		return intercepted;
	}

	public void close() {
		// TODO Auto-generated method stub
		// 无操作
	}

	public static class Builder implements Interceptor.Builder {

		private Pattern regex;
		private List<NameAndSerializer> serializerList;
		private final RegexExtractorInterceptorSerializer defaultSerializer = new RegexExtractorInterceptorPassThroughSerializer();

		@Override
		public Interceptor build() {
			// TODO Auto-generated method stub
			Preconditions.checkArgument(this.regex != null, "Regex pattern was misconfigured");
			Preconditions.checkArgument(this.serializerList.size() > 0, "Must supply a valid group match id list");

			return new RegexExtractorInterceptor(this.regex, this.serializerList);
		}

		@Override
		public void configure(Context context) {
			// TODO Auto-generated method stub
			String regexString = context.getString(REGEX);
			Preconditions.checkArgument(!StringUtils.isEmpty(regexString), "Must supply a valid regex string");

			this.regex = Pattern.compile(regexString);
			this.regex.pattern();
			this.regex.matcher("").groupCount();
			this.configureSerializers(context);
		}

		/**
		 * 通过读取配置，将Serializer组进行初始化，并将所有的Serializer存入this.serializerList中
		 * 
		 * @param context
		 */
		private void configureSerializers(Context context) {
			// 配置文件中serializer是写在一起的，中间以空格分隔
			String serializerListStr = context.getString(SERIALIZERS);
			Preconditions.checkArgument(StringUtils.isEmpty(serializerListStr), "Must supply at least one name and serializer");
			// 将serializer提取出来放入数组
			String[] serializerNames = serializerListStr.split("\\s+");

			// 得到每个serializer的配置
			// agent.sources.r1.interceptors.i1.serializers.s1.name = one
			Context serializerContexts = new Context(context.getSubProperties(SERIALIZERS + "."));

			// 初始化List
			this.serializerList = Lists.newArrayListWithCapacity(serializerNames.length);
			for (String serializerName : serializerNames) {
				Context serializerContext = new Context(serializerContexts.getSubProperties(serializerName + "."));
				String type = serializerContext.getString("type", "DEFAULT");
				String name = serializerContext.getString("name");
				Preconditions.checkArgument(!StringUtils.isEmpty(name), "Supplied name cannot be empty.");

				if ("DEFAULT".equals(type)) {
					this.serializerList.add(new NameAndSerializer(name, this.defaultSerializer));
				} else {
					serializerList.add(new NameAndSerializer(name, this.getCustomSerializer(type, serializerContext)));
				}
			}

		}

		/**
		 * 实例化RegexExtractorInterceptorSerializer
		 * 
		 * @param clazzName
		 *            给定的Serializer类名
		 * @param context
		 *            当前Serializer的配置信息
		 * @return
		 */
		@SuppressWarnings("deprecation")
		private RegexExtractorInterceptorSerializer getCustomSerializer(String clazzName, Context context) {
			try {
				RegexExtractorInterceptorSerializer serializer = (RegexExtractorInterceptorSerializer) Class.forName(clazzName)
						.newInstance();
				serializer.configure(context);

				return serializer;
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("Could not instantiate event serializer.", e);
				Throwables.propagate(e);
			}
			return this.defaultSerializer;
		}
	}

	/**
	 * 
	 * @author
	 *
	 */
	public static class NameAndSerializer {
		private final String headerName;
		private final RegexExtractorInterceptorSerializer serializer;

		/**
		 * 构造函数
		 * 
		 * @param headerName
		 * @param serializer
		 */
		public NameAndSerializer(String headerName, RegexExtractorInterceptorSerializer serializer) {
			// TODO Auto-generated constructor stub
			this.headerName = headerName;
			this.serializer = serializer;
		}
	}
}

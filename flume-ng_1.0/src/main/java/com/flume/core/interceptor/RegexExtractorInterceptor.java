package com.flume.core.interceptor;

import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.sdk.Event;

/**
 * Interceptor that extracts matches using a specified regular expression and
 * appends the matches to the event headers using the specified serializers
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
 * agent.sources.r1.interceptors.i1.serializers.s1.type =
 * com.blah.SomeSerializer agent.sources.r1.interceptors.i1.serializers.s1.name
 * = warning agent.sources.r1.interceptors.i1.serializers.s2.type =
 * org.apache.flume.interceptor.RegexExtractorInterceptorTimestampSerializer
 * agent.sources.r1.interceptors.i1.serializers.s2.name = error
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
		return null;
	}

	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() {
		// TODO Auto-generated method stub
		// 无操作
	}

	public static class NameAndSerializer{
		private final String headerName;
		private final Rege
	}
}

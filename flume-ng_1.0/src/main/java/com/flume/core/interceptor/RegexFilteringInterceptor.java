package com.flume.core.interceptor;

import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.sdk.Event;
import com.google.common.collect.Lists;

import static com.flume.core.interceptor.RegexFilteringInterceptor.Constants.REGEX;
import static com.flume.core.interceptor.RegexFilteringInterceptor.Constants.REGEX_DEFAULT;
import static com.flume.core.interceptor.RegexFilteringInterceptor.Constants.EXCLUDE_EVENTS;
import static com.flume.core.interceptor.RegexFilteringInterceptor.Constants.EXCLUUDE_EVENTS_DEFAULT;

/**
 * Interceptor that filters events selectively based on a configured regular
 * expression matching against the event body.
 *
 * This supports either include- or exclude-based filtering. A given interceptor
 * can only perform one of these functions, but multiple interceptor can be
 * chained together to create more complex inclusion/exclusion patterns. If
 * include-based filtering is configured, then all events matching the supplied
 * regular expression will be passed through and all events not matching will be
 * ignored. If exclude-based filtering is configured, than all events matching
 * will be ignored, and all other events will pass through.
 *
 * Note that all regular expression matching occurs through Java's built in
 * java.util.regex package.
 *
 * Properties:
 * <p>
 *
 * regex: Regular expression for matching excluded events. (default is ".*")
 * <p>
 *
 * excludeEvents: If true, a regex match determines events to exclude, otherwise
 * a regex determines events to include (default is false)
 * <p>
 *
 * Sample config:
 * <p>
 *
 * <code>
 *   agent.sources.r1.channels = c1<p>
 *   agent.sources.r1.type = SEQ<p>
 *   agent.sources.r1.interceptors = i1<p>
 *   agent.sources.r1.interceptors.i1.type = REGEX<p>
 *   agent.sources.r1.interceptors.i1.regex = (WARNING)|(ERROR)|(FATAL)<p>
 * </code>
 *
 */
public class RegexFilteringInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(RegexFilteringInterceptor.class);

	private final Pattern regex;
	/**
	 * 若为true，则event满足正则表达式的时候，将event移除；若为false，则event满足正则表达式的时候，将event留下
	 */
	private final boolean excludeEvents;

	/**
	 * <p>
	 * 构造函数
	 * </p>
	 * Only {@link RegexFilteringInterceptor.Builder} can build me
	 */
	private RegexFilteringInterceptor(Pattern regex, boolean excludeEvents) {
		// TODO Auto-generated constructor stub
		this.regex = regex;
		this.excludeEvents = excludeEvents;
	}

	public void initialize() {
		// TODO Auto-generated method stub
		// 无操作
	}

	/**
	 * Returns the event if it passes the regular expression filter and null
	 * otherwise.
	 */
	public Event intercept(Event event) {
		// TODO Auto-generated method stub
		// We've already ensured here that at most one of includeRegex and
		// excludeRegex are defined.
		if (!excludeEvents) {
			if (regex.matcher(new String(event.getBody())).find()) {
				return event;
			} else {
				return null;
			}
		} else {
			if (regex.matcher(new String(event.getBody())).find()) {
				return null;
			} else {
				return event;
			}
		}
	}

	/**
	 * Returns the set of events which pass filters, according to
	 * {@link #intercept(Event)}.
	 * 
	 * @param events
	 * @return
	 */
	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		List<Event> out = Lists.newArrayList();
		for (Event event : events) {
			Event outEvent = this.intercept(event);
			if (outEvent != null) {
				out.add(outEvent);
			}
		}
		return out;
	}

	public void close() {
		// TODO Auto-generated method stub
		// 无操作
	}

	/**
	 * Builder which builds new instance of the StaticInterceptor.
	 */
	public static class Builder implements Interceptor.Builder {
		private Pattern regex;
		private boolean excludeEvents;

		public Interceptor build() {
			// TODO Auto-generated method stub
			logger.info(String.format("Creating RegexFilteringInterceptor: regex=%s,excludeEvents=%s", regex, excludeEvents));
			return new RegexFilteringInterceptor(regex, excludeEvents);
		}

		public void configure(Context context) {
			// TODO Auto-generated method stub
			String regexString = context.getString(REGEX, REGEX_DEFAULT);
			regex = Pattern.compile(regexString);
			excludeEvents = context.getBoolean(EXCLUDE_EVENTS, EXCLUUDE_EVENTS_DEFAULT);
		}
	}

	public static class Constants {
		public static final String REGEX = "regex";
		public static final String REGEX_DEFAULT = ".*";

		public static final String EXCLUDE_EVENTS = "excludeEvents";
		public static final boolean EXCLUUDE_EVENTS_DEFAULT = false;
	}
}

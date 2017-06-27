package com.flume.core.interceptor;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.sdk.Event;

/**
 * Interceptor class that appends a static, pre-configured header to all events.
 *
 * Properties:
 * <p>
 *
 * key: Key to use in static header insertion. (default is "key")
 * <p>
 *
 * value: Value to use in static header insertion. (default is "value")
 * <p>
 *
 * preserveExisting: Whether to preserve an existing value for 'key' (default is
 * true)
 * <p>
 *
 * Sample config:
 * <p>
 *
 * <code>
 *   agent.sources.r1.channels = c1<p>
 *   agent.sources.r1.type = SEQ<p>
 *   agent.sources.r1.interceptors = i1<p>
 *   agent.sources.r1.interceptors.i1.type = static<p>
 *   agent.sources.r1.interceptors.i1.preserveExisting = false<p>
 *   agent.sources.r1.interceptors.i1.key = datacenter<p>
 *   agent.sources.r1.interceptors.i1.value= NYC_01<p>
 * </code>
 * 
 * @author
 *
 */
public class StaticInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(StaticInterceptor.class);

	private final boolean preserveExisting;
	private final String key;
	private final String value;

	/**
	 * <p>
	 * 构造函数
	 * </p>
	 * Only {@link HostInterceptor.Builder} can build me
	 */
	private StaticInterceptor(boolean preserveExisting, String key, String value) {
		// TODO Auto-generated constructor stub
		this.preserveExisting = preserveExisting;
		this.key = key;
		this.value = value;
	}

	public void initialize() {
		// TODO Auto-generated method stub
		// 无操作
	}

	/**
	 * Modifies events in-place.
	 */
	public Event intercept(Event event) {
		// TODO Auto-generated method stub
		Map<String, String> headers = event.getHeaders();

		if (preserveExisting && headers.containsKey(key)) {
			return event;
		}
		headers.put(key, value);
		return event;
	}

	/**
	 * Delegates to {@link #intercept(Event)} in a loop.
	 * 
	 * @param events
	 * @return
	 */
	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		for (Event event : events) {
			this.intercept(event);
		}
		return events;
	}

	public void close() {
		// TODO Auto-generated method stub
		// 无操作
	}

	public static class Builder implements Interceptor.Builder {

		private boolean preserveExisting;
		private String key;
		private String value;

		public Interceptor build() {
			// TODO Auto-generated method stub
			logger.info(String.format("Creating StaticInterceptor: preserveExisting=%s,key=%s,value=%s", preserveExisting, key, value));

			return new StaticInterceptor(preserveExisting, key, value);
		}

		public void configure(Context context) {
			// TODO Auto-generated method stub
			preserveExisting = context.getBoolean(Constants.PRESERVE, Constants.PRESERVE_DEFAULT);
			key = context.getString(Constants.KEY, Constants.KEY_DEFAULT);
			value = context.getString(Constants.VALUE, Constants.VALUE_DEFAULT);
		}
	}

	public static class Constants {
		public static final String KEY = "key";
		public static final String KEY_DEFAULT = "key";

		public static final String VALUE = "value";
		public static final String VALUE_DEFAULT = "value";

		public static final String PRESERVE = "preserveExisting";
		public static final boolean PRESERVE_DEFAULT = true;
	}
}

package com.flume.core.interceptor;

import java.util.List;
import java.util.Map;

import com.flume.configuration.Context;
import com.flume.sdk.Event;
import static com.flume.core.interceptor.TimestampInterceptor.Constants.*;

/**
 * Simple Interceptor class that sets the current system timestamp on all events
 * that are intercepted. By convention, this timestamp header is named
 * "timestamp" and its format is a "stringified" long timestamp in milliseconds
 * since the UNIX epoch.
 * <p>
 * 简单的拦截器类，用于在被拦截的所有事件上设置当前系统时间戳。
 * 按照惯例，这个时间戳头被命名为“时间戳”，它的格式是自UNIX时代以来以毫秒为单位的“串化”长时间戳。
 * </p>
 * 
 * @author
 *
 */
public class TimestampInterceptor implements Interceptor {

	/**
	 * 是否保持原来event的时间戳记录，若为true，则若源event有时间戳记录，则不修改，其余情况则修改为当前时间戳
	 */
	private final boolean preserveExisting;

	/**
	 * 构造函数 Only {@link TimestampInterceptor.Builder} can build me
	 * 
	 * @param preserveExisting
	 */
	private TimestampInterceptor(boolean preserveExisting) {
		this.preserveExisting = preserveExisting;
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
		if (preserveExisting && headers.containsKey(TIMESTAMP)) {
			// we must preserve the existing timestamp
			// 不做修改
		} else {
			long now = System.currentTimeMillis();
			headers.put(TIMESTAMP, Long.toString(now));
		}
		return event;
	}

	/**
	 * Delegates(委托) to {@link #intercept(Event)} in a loop.
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

	/**
	 * Builder which builds new instances of the TimestampInterceptor.
	 * 
	 * @author
	 *
	 */
	public static class Builder implements Interceptor.Builder {
		private boolean preserveExisting = PRESERVE_DELF;

		public Interceptor build() {
			// TODO Auto-generated method stub
			return new TimestampInterceptor(preserveExisting);
		}

		public void configure(Context context) {
			// TODO Auto-generated method stub
			preserveExisting = context.getBoolean(PRESERVE, PRESERVE_DELF);
		}
	}

	public static class Constants {
		public static String TIMESTAMP = "timestamp";
		public static String PRESERVE = "preserveExisting";
		public static boolean PRESERVE_DELF = false;
	}
}

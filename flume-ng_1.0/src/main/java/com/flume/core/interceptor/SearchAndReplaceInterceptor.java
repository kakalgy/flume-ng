package com.flume.core.interceptor;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.sdk.Event;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

/**
 * <p>
 * Interceptor that allows search-and-replace of event body strings using
 * regular expressions. This only works with event bodies that are valid
 * strings. The charset is configurable.
 * <p>
 * 这个拦截器使用来替换Event里body中的字符串，通过正则表达式匹配，将匹配到的值替换为配置的值，同时，字符串的编码是可以配置的
 * <p>
 * Usage:
 * 
 * <pre>
 *   agent.source-1.interceptors.search-replace.searchPattern = ^INFO:
 *   agent.source-1.interceptors.search-replace.replaceString = Log msg:
 * </pre>
 * <p>
 * Any regular expression search pattern and replacement pattern that can be
 * used with {@link java.util.regex.Matcher#replaceAll(String)} may be used,
 * including backtracking and grouping.
 */
public class SearchAndReplaceInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(SearchAndReplaceInterceptor.class);

	private final Pattern searchPattern;
	private final String replaceString;
	private final Charset charset;

	/**
	 * 
	 * 构造函数
	 *
	 * @param searchPattern
	 * @param replaceString
	 * @param charset
	 */
	private SearchAndReplaceInterceptor(Pattern searchPattern, String replaceString, Charset charset) {
		// TODO Auto-generated constructor stub
		this.searchPattern = searchPattern;
		this.replaceString = replaceString;
		this.charset = charset;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		// 无操作
	}

	@Override
	public Event intercept(Event event) {
		// TODO Auto-generated method stub
		String oriBody = new String(event.getBody(), this.charset);
		Matcher matcher = this.searchPattern.matcher(oriBody);
		String newBody = matcher.replaceAll(this.replaceString);
		event.setBody(newBody.getBytes(this.charset));
		return event;
	}

	@Override
	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		for (Event event : events) {
			this.intercept(event);
		}
		return events;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		// 无操作
	}

	public static class Builder implements Interceptor.Builder {
		private static final String SEARCH_PAT_KEY = "searchPattern";
		private static final String REPLACE_STRING_KEY = "replaceString";
		private static final String CHARSET_KEY = "charset";

		private Pattern searchRegex;
		private String replaceString;
		private Charset charset = Charsets.UTF_8;

		@Override
		public Interceptor build() {
			// TODO Auto-generated method stub
			Preconditions.checkNotNull(this.searchRegex, "Regular expression search pattern required");
			Preconditions.checkNotNull(this.replaceString, "Replacement string required");
			return new SearchAndReplaceInterceptor(this.searchRegex, this.replaceString, this.charset);
		}

		@Override
		public void configure(Context context) {
			// TODO Auto-generated method stub
			String searchPattern = context.getString(SEARCH_PAT_KEY);
			Preconditions.checkArgument(!StringUtils.isEmpty(searchPattern),
					"Must supply a valid search pattern " + SEARCH_PAT_KEY + " (may not be empty)");

			this.replaceString = context.getString(REPLACE_STRING_KEY);
			// Empty replacement String value or if the property itself is not
			// present
			// assign empty string as replacement
			if (this.replaceString == null) {
				this.replaceString = "";
			}

			this.searchRegex = Pattern.compile(searchPattern);

			if (context.containKey(CHARSET_KEY)) {
				// May throw IllegalArgumentException for unsupported charsets.
				this.charset = Charset.forName(context.getString(CHARSET_KEY));
			}
		}
	}
}

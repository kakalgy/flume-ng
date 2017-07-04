package com.flume.core.interceptor;

public enum InterceptorType {

	TIMESTAMP(com.flume.core.interceptor.TimestampInterceptor.Builder.class), HOST(com.flume.core.interceptor.HostInterceptor.Builder.class), STATIC(
			com.flume.core.interceptor.StaticInterceptor.Builder.class), REGEX_FILTER(
					com.flume.core.interceptor.RegexFilteringInterceptor.Builder.class), REGEX_EXTRACTOR(
							com.flume.core.interceptor.RegexExtractorInterceptor.Builder.class), SEARCH_REPLACE(
									com.flume.core.interceptor.SearchAndReplaceInterceptor.Builder.class);

	private final Class<? extends Interceptor.Builder> builderClass;

	/**
	 * 
	 * @param builderClass
	 */
	private InterceptorType(Class<? extends Interceptor.Builder> builderClass) {
		this.builderClass = builderClass;
	}

	public Class<? extends Interceptor.Builder> getBuilderClass() {
		return builderClass;
	}
}

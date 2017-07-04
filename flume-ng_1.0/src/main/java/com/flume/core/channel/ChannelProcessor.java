package com.flume.core.channel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.core.ChannelSelector;
import com.flume.core.conf.Configurable;
import com.flume.core.interceptor.Interceptor;
import com.flume.core.interceptor.InterceptorBuilderFactory;
import com.flume.core.interceptor.InterceptorChain;
import com.flume.sdk.FlumeException;
import com.google.common.collect.Lists;

/**
 * A channel processor exposes operations to put {@link Event}s into
 * {@link Channel}s. These operations will propagate a {@link ChannelException}
 * if any errors occur while attempting to write to {@code required} channels.
 * <p>
 * Each channel processor instance is configured with a {@link ChannelSelector}
 * instance that specifies which channels are
 * {@linkplain ChannelSelector#getRequiredChannels(Event) required} and which
 * channels are {@linkplain ChannelSelector#getOptionalChannels(Event)
 * optional}.
 * <p>
 * 每个ChannelProcessor暴露操作将event推送如channel，如果在向required的channel写问event的时候出现任何错误都会抛出ChannelException
 * <p>
 * 每个ChannelProcessor实例都是通过ChannelSelector实例来进行配置，配置哪些channel是required，哪些channel是optional
 */
public class ChannelProcessor implements Configurable {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelProcessor.class);

	private final ChannelSelector selector;
	private final InterceptorChain interceptorChain;

	/**
	 * 构造函数
	 * 
	 * @param selector
	 */
	public ChannelProcessor(ChannelSelector selector) {
		// TODO Auto-generated constructor stub
		this.selector = selector;
		this.interceptorChain = new InterceptorChain();
	}

	/**
	 * 遍历拦截器链中的所有拦截器，进行初始化（应该在设置好拦截器链后再调用此方法）
	 */
	public void initialize() {
		this.interceptorChain.initialize();
	}

	/**
	 * 遍历拦截器链中的所有拦截器，进行关闭拦截器（应该在设置好拦截器链后再调用此方法）
	 */
	public void close() {
		this.interceptorChain.close();
	}

	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub

	}

	private void configureInterceptors(Context context) {
		List<Interceptor> interceptors = Lists.newLinkedList();

		String interceptorListStr = context.getString("interceptors", "");
		if (interceptorListStr.isEmpty()) {
			return;
		}

		String[] interceptorNames = interceptorListStr.split("\\s+");

		Context interceptorContexts = new Context(context.getSubProperties("interceptors."));

		// run through and instantiate all the interceptors specified in the
		// Context
		InterceptorBuilderFactory factory = new InterceptorBuilderFactory();
		for (String interceptorName : interceptorNames) {
			Context interceptorContext = new Context(interceptorContexts.getSubProperties(interceptorName + "."));
			String type = interceptorContext.getString("type");
			if (type == null) {
				LOG.error("Type not specified for interceptor " + interceptorName);
				throw new FlumeException("Interceptor.Type not specified for " + interceptorName);
			}
			
			try{
				Interceptor.Builder builder = factory.newInstance(type);
				
			}
		}
	}

	/****************************** Get/Set方法 *********************************/
	public ChannelSelector getSelector() {
		return selector;
	}
}

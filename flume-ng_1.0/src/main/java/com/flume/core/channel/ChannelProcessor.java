package com.flume.core.channel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.core.Channel;
import com.flume.core.ChannelException;
import com.flume.core.ChannelSelector;
import com.flume.core.Transaction;
import com.flume.core.conf.Configurable;
import com.flume.core.interceptor.Interceptor;
import com.flume.core.interceptor.InterceptorBuilderFactory;
import com.flume.core.interceptor.InterceptorChain;
import com.flume.sdk.Event;
import com.flume.sdk.FlumeException;
import com.google.common.base.Preconditions;
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
		this.configureInterceptors(context);
	}

	/**
	 * 通过配置文件将所有的拦截器实例化，并保存到interceptorChain中
	 * 
	 * @param context
	 */
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

			try {
				Interceptor.Builder builder = factory.newInstance(type);
				builder.configure(interceptorContext);
				interceptors.add(builder.build());
			} catch (ClassNotFoundException e) {
				LOG.error("Builder class not found. Exception follows.", e);
				throw new FlumeException("Interceptor.Builder not found.", e);
			} catch (InstantiationException e) {
				LOG.error("Could not instantiate Builder. Exception follows.", e);
				throw new FlumeException("Interceptor.Builder not constructable.", e);
			} catch (IllegalAccessException e) {
				LOG.error("Unable to access Builder. Exception follows.", e);
				throw new FlumeException("Unable to access Interceptor.Builder.", e);
			}
		}

		this.interceptorChain.setInterceptors(interceptors);
	}

	/**
	 * 针对的List<Event>
	 * <p>
	 * Attempts to {@linkplain Channel#put(Event) put} the given events into
	 * each configured channel. If any {@code required} channel throws a
	 * {@link ChannelException}, that exception will be propagated(传播).
	 * <p>
	 * 当将event推入到每一个配置好的channel时，如果有任何一个required的channel抛出了ChannelException异常，则这个异常会传播
	 * <p>
	 * <p>
	 * Note that if multiple channels are configured, some {@link Transaction}s
	 * may have already been committed while others may be rolled back in the
	 * case of an exception.
	 *
	 * @param events
	 *            A list of events to put into the configured channels.
	 * @throws ChannelException
	 *             when a write to a required channel fails.
	 */
	public void processEventBatch(List<Event> events) {

		Preconditions.checkNotNull(events, "Event list must not be null");

		// 将event经过拦截器进行过滤
		events = this.interceptorChain.intercept(events);

		// required channels
		Map<Channel, List<Event>> reqChannelQueue = new LinkedHashMap<>();
		// optional channels
		Map<Channel, List<Event>> optChannelQueue = new LinkedHashMap<>();

		//
		for (Event event : events) {
			List<Channel> reqChannels = this.selector.getRequiredChannels(event);

			for (Channel ch : reqChannels) {
				List<Event> eventQueue = reqChannelQueue.get(ch);
				if (eventQueue == null) {
					eventQueue = new ArrayList<>();
					reqChannelQueue.put(ch, eventQueue);
				}
				eventQueue.add(event);
			}

			List<Channel> optChannels = this.selector.getOptionalChannels(event);

			for (Channel ch : optChannels) {
				List<Event> eventQueue = optChannelQueue.get(ch);
				if (eventQueue == null) {
					eventQueue = new ArrayList<>();
					optChannelQueue.put(ch, eventQueue);
				}
				eventQueue.add(event);
			}
		}

		// Process required channels
		// 使用到自定义事务处理，将reqChannelQueue中的每个channel需要推送的event进行推送
		for (Channel reqChannel : reqChannelQueue.keySet()) {
			Transaction tx = reqChannel.getTransaction();
			Preconditions.checkNotNull(tx, "Transaction object must not be null");

			try {
				tx.begin();

				List<Event> batchEvents = reqChannelQueue.get(reqChannel);

				for (Event event : batchEvents) {
					reqChannel.put(event);
				}

				tx.commit();
			} catch (Throwable t) {
				tx.rollback();

				if (t instanceof Error) {
					LOG.error("Error while writing to required channel: " + reqChannel, t);
					throw (Error) t;
				} else if (t instanceof ChannelException) {
					throw (ChannelException) t;
				} else {
					throw new ChannelException("Unable to put batch on required " + "channel: " + reqChannel, t);
				}
			} finally {
				if (tx != null) {
					tx.close();
				}
			}
		}

		// Process optional channels
		// 使用到自定义事务处理，将optChannelQueue中的每个channel需要推送的event进行推送
		for (Channel optChannel : optChannelQueue.keySet()) {
			Transaction tx = optChannel.getTransaction();
			Preconditions.checkNotNull(tx, "Transaction object must not be null");

			try {
				tx.begin();

				List<Event> batchEvents = optChannelQueue.get(optChannel);

				for (Event event : batchEvents) {
					optChannel.put(event);
				}

				tx.commit();
			} catch (Throwable t) {
				// TODO: handle exception
				tx.rollback();
				LOG.error("Unable to put batch on optional channel: " + optChannel, t);

				if (t instanceof Error) {
					throw (Error) t;
				}
			} finally {
				if (tx != null) {
					tx.close();
				}
			}
		}
	}

	/**
	 * 针对单个Event
	 * <p>
	 * Attempts to {@linkplain Channel#put(Event) put} the given event into each
	 * configured channel. If any {@code required} channel throws a
	 * {@link ChannelException}, that exception will be propagated.
	 * <p>
	 * <p>
	 * Note that if multiple channels are configured, some {@link Transaction}s
	 * may have already been committed while others may be rolled back in the
	 * case of an exception.
	 *
	 * @param event
	 *            The event to put into the configured channels.
	 * @throws ChannelException
	 *             when a write to a required channel fails.
	 */
	public void processEvent(Event event) {

		event = this.interceptorChain.intercept(event);
		if (event == null) {
			return;
		}

		// Process required channels
		List<Channel> reqChannels = this.selector.getRequiredChannels(event);

		for (Channel reqChannel : reqChannels) {
			Transaction tx = reqChannel.getTransaction();
			Preconditions.checkNotNull(tx, "Transaction object must not be null");
			try {
				tx.begin();

				reqChannel.put(event);

				tx.commit();
			} catch (Throwable t) {
				tx.rollback();
				if (t instanceof Error) {
					LOG.error("Error while writing to required channel: " + reqChannel, t);
					throw (Error) t;
				} else if (t instanceof ChannelException) {
					throw (ChannelException) t;
				} else {
					throw new ChannelException("Unable to put event on required " + "channel: " + reqChannel, t);
				}
			} finally {
				// TODO: handle finally clause
				if (tx != null) {
					tx.close();
				}
			}
		}

		// Process optional channels
		List<Channel> optionalChannels = selector.getOptionalChannels(event);
		for (Channel optChannel : optionalChannels) {
			Transaction tx = null;
			try {
				tx = optChannel.getTransaction();
				tx.begin();

				optChannel.put(event);

				tx.commit();
			} catch (Throwable t) {
				tx.rollback();
				LOG.error("Unable to put event on optional channel: " + optChannel, t);
				if (t instanceof Error) {
					throw (Error) t;
				}
			} finally {
				if (tx != null) {
					tx.close();
				}
			}
		}
	}

	/****************************** Get/Set方法 *********************************/
	public ChannelSelector getSelector() {
		return selector;
	}
}

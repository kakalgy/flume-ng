package com.flume.core.interceptor;

import java.util.Iterator;
import java.util.List;

import com.flume.sdk.Event;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Implementation of Interceptor that calls a list of other Interceptors
 * serially(连续的).
 * <p>
 * 拦截器链
 * </p>
 * 
 * @author
 *
 */
public class InterceptorChain implements Interceptor {

	// list of interceptors that will be traversed(穿越), in order
	private List<Interceptor> interceptors;

	/**
	 * 构造函数
	 */
	public InterceptorChain() {
		// TODO Auto-generated constructor stub
		// 因为拦截器是有序排列的，所以用到的是LinkedList
		this.interceptors = Lists.newLinkedList();
	}

	public void setInterceptors(List<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}

	/************************** 实现Interceptor接口方法 ******************************/
	public Event intercept(Event event) {
		// TODO Auto-generated method stub
		for (Interceptor interceptor : interceptors) {
			if (event == null) {
				return null;
			}
			event = interceptor.intercept(event);
		}
		return event;
	}

	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		for (Interceptor interceptor : interceptors) {
			if (events.isEmpty()) {
				return events;
			}
			events = interceptor.intercept(events);
			Preconditions.checkNotNull(events, "Event list returned null from interceptor %s", interceptor);
		}
		return events;
	}

	public void initialize() {
		// TODO Auto-generated method stub
		Iterator<Interceptor> iter = this.interceptors.iterator();
		while (iter.hasNext()) {
			Interceptor interceptor = iter.next();
			interceptor.initialize();
		}
	}

	public void close() {
		// TODO Auto-generated method stub
		Iterator<Interceptor> iter = this.interceptors.iterator();
		while (iter.hasNext()) {
			Interceptor interceptor = iter.next();
			interceptor.close();
		}
	}
}

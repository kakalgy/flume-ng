package com.flume.core.interceptor;

import java.util.List;

import com.flume.core.annotations.InterfaceAudience;
import com.flume.core.annotations.InterfaceStability;
import com.flume.core.conf.Configurable;
import com.flume.sdk.Event;

/**
 * event拦截器
 * 
 * @author
 *
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Interceptor {

	/**
	 * Any initialization / startup needed by the Interceptor.
	 */
	public void initialize();

	/**
	 * Interception of a single {@link Event}.
	 * 
	 * @param event
	 *            Event to be intercepted
	 * @return Original or modified event, or {@code null} if the Event is to be
	 *         dropped (i.e. filtered out).
	 */
	public Event intercept(Event event);

	/**
	 * Interception of a batch of {@linkplain Event events}.
	 * 
	 * @param events
	 *            Input list of events
	 * @return Output list of events. The size of output list MUST NOT BE
	 *         GREATER than the size of the input list (i.e. transformation and
	 *         removal ONLY). Also, this method MUST NOT return {@code null}. If
	 *         all events are dropped, then an empty List is returned.
	 *         <p>
	 *         注意事项：返回的List元素数量不得大于参数的元素数量，且不会返回null，即使没有event，也会返回空的List
	 *         </p>
	 */
	public List<Event> intercept(List<Event> events);

	/**
	 * Perform any closing / shutdown needed by the Interceptor.
	 */
	public void close();

	/**
	 * Builder implementations MUST have a no-arg constructor
	 * 
	 * @author
	 *
	 */
	public interface Builder extends Configurable {
		public Interceptor build();
	}
}

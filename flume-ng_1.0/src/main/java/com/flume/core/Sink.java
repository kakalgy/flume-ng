package com.flume.core;

import com.flume.core.annotations.InterfaceAudience;
import com.flume.core.annotations.InterfaceStability;
import com.flume.core.lifecycle.LifecycleAware;
import com.flume.sdk.EventDeliveryException;

/**
 * <p>
 * A sink is connected to a <tt>Channel</tt> and consumes its contents, sending
 * them to a configured destination that may vary according to the sink type.
 * </p>
 * <p>
 * Sinks can be grouped together for various behaviors using <tt>SinkGroup</tt>
 * and <tt>SinkProcessor</tt>. They are polled periodically by a
 * <tt>SinkRunner</tt> via the processor
 * </p>
 * <p>
 * Sinks are associated with unique names that can be used for separating
 * configuration and working namespaces.
 * </p>
 * <p>
 * While the {@link Sink#process()} call is guaranteed to only be accessed by a
 * single thread, other calls may be concurrently accessed and should thus be
 * protected.
 * </p>
 *
 * @see org.apache.flume.Channel
 * @see org.apache.flume.SinkProcessor
 * @see org.apache.flume.SinkRunner
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Sink extends LifecycleAware, NamedComponent {
	/**
	 * <p>
	 * Sets the channel the sink will consume from
	 * </p>
	 * 
	 * @param channel
	 *            The channel to be polled
	 */
	public void setChannel(Channel channel);

	/**
	 * <p>
	 * Requests the sink to attempt to consume(消耗) data from attached channel
	 * </p>
	 * <p>
	 * <strong>Note</strong>: This method should be consuming from the channel
	 * within the bounds of a Transaction. On successful delivery, the
	 * transaction should be committed, and on failure it should be rolled back.
	 * 
	 * @return READY if 1 or more Events were successfully delivered, BACKOFF if
	 *         no data could be retrieved(恢复) from the channel feeding this sink
	 * @throws EventDeliveryException
	 *             In case of any kind of failure to deliver data to the next
	 *             hop destination.
	 */
	public Status process() throws EventDeliveryException;

	/**
	 * @return the channel associated with this sink
	 */
	public Channel getChannel();

	public static enum Status {
		READY, BACKOFF
	}
}

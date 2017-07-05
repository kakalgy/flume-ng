package com.flume.core;

import java.util.List;

import com.flume.core.Sink.Status;
import com.flume.core.conf.Configurable;
import com.flume.core.lifecycle.LifecycleAware;
import com.flume.sdk.EventDeliveryException;

/**
 * <p>
 * Interface for a device that allows abstraction of the behavior of multiple
 * sinks, always assigned to a SinkRunner
 * </p>
 * <p>
 * 接口用于允许抽象多个sink的行为的设备，总是分配给SinkRunner
 * </p>
 * <p>
 * A sink processors {@link SinkProcessor#process()} method will only be
 * accessed by a single runner thread. However configuration methods such as
 * {@link Configurable#configure} may be concurrently accessed.
 *
 * @see org.apache.flume.Sink
 * @see org.apache.flume.SinkRunner
 * @see org.apache.flume.sink.SinkGroup
 */
public interface SinkProcessor extends LifecycleAware, Configurable {
	/**
	 * <p>
	 * Handle a request to poll the owned sinks.
	 * </p>
	 *
	 * <p>
	 * The processor is expected to call {@linkplain Sink#process()} on whatever
	 * sink(s) appropriate, handling failures as appropriate and throwing
	 * {@link EventDeliveryException} when there is a failure to deliver any
	 * events according to the delivery policy defined by the sink processor
	 * implementation. See specific implementations of this interface for
	 * delivery behavior and policies.
	 * </p>
	 *
	 * @return Returns {@code READY} if events were successfully consumed, or
	 *         {@code BACKOFF} if no events were available in the channel to
	 *         consume.
	 * @throws EventDeliveryException
	 *             if the behavior guaranteed by the processor couldn't be
	 *             carried out.
	 */
	Status process() throws EventDeliveryException;

	/**
	 * <p>
	 * Set all sinks to work with.
	 * </p>
	 *
	 * <p>
	 * Sink specific parameters are passed to the processor via configure
	 * </p>
	 *
	 * @param sinks
	 *            A non-null, non-empty list of sinks to be chosen from by the
	 *            processor
	 */
	void setSinks(List<Sink> sinks);
}

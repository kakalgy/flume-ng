package com.flume.core;

import com.flume.sdk.EventDeliveryException;

/**
 * A {@link Source} that requires an external driver to poll to determine
 * whether there are {@linkplain Event events} that are available to ingest from
 * the source.
 * <p>
 * 这个Source需要一个外部驱动来检测决定从source中获取可用的Event
 *
 * @see org.apache.flume.source.EventDrivenSourceRunner
 */
public interface PollableSource extends Source {
	/**
	 * <p>
	 * Attempt to pull an item from the source, sending it to the channel.
	 * </p>
	 * <p>
	 * 尝试从source中取出一个数据，将它发送到channel
	 * </p>
	 * <p>
	 * 当驱动使用EventDrivenSourceRunner来处理时，则保证每次只能由一个线程调用，没有并发；其他所有的机制来驱动PollableSource时必须遵循相同的语义。
	 * </p>
	 * <p>
	 * When driven by an {@link EventDrivenSourceRunner} process is guaranteed
	 * to be called only by a single thread at a time, with no concurrency. Any
	 * other mechanism driving a pollable source must follow the same semantics.
	 * </p>
	 * 
	 * @return {@code READY} if one or more events were created from the source.
	 *         {@code BACKOFF} if no events could be created from the source.
	 * @throws EventDeliveryException
	 *             If there was a failure in delivering to the attached channel,
	 *             or if a failure occurred in acquiring data from the source.
	 */
	public Status process() throws EventDeliveryException;

	/**
	 * 
	 * @return
	 */
	public long getBackOffSleepIncrement();

	/**
	 * 
	 * @return
	 */
	public long getMaxBackOffSleepInterval();

	public static enum Status {
		READY, BACKOFF
	}
}

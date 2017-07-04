package com.flume.core;

/**
 * A {@link Source} that does not need an external driver to poll for
 * {@linkplain Event events} to ingest; it provides its own event-driven
 * mechanism to invoke event processing.
 * <p>
 * 这个Source不需要一个外部的驱动来检测要获取的Event，它提供了自己的事件驱动机制来调用事件处理。
 */
public interface EventDrivenSource extends Source {

}

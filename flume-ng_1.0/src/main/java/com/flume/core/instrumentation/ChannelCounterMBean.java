package com.flume.core.instrumentation;

/**
 * This interface represents a channel counter mbean. Any class implementing
 * this interface must sub-class
 * {@linkplain org.apache.flume.instrumentation.MonitoredCounterGroup}. This
 * interface might change between minor releases. Please see
 * {@linkplain org.apache.flume.instrumentation.ChannelCounter} class.
 * 
 * @Description 实现这个接口 必须要继承MonitoredCounterGroup类
 * @author Administrator
 * @date 2017年8月8日 下午9:15:04
 *
 */
public interface ChannelCounterMBean {
	/**
	 * 
	 * @Description 已经使用的容量大小
	 * @return
	 */
	long getChannelSize();

	/**
	 * 
	 * @Description source到channel尝试插入的数据（不管是否成功）
	 * @return
	 */
	long getEventPutAttemptCount();

	/**
	 * 
	 * @Description sink从channel尝试消费的数据（不管是否成功）
	 * @return
	 */
	long getEventTakeAttemptCount();

	/**
	 * 
	 * @Description source到channel成功插入的数据
	 * @return
	 */
	long getEventPutSuccessCount();

	/**
	 * 
	 * @Description sink从channel成功消费的数据
	 * @return
	 */
	long getEventTakeSuccessCount();

	long getStartTime();

	long getStopTime();

	/**
	 * 
	 * @Description 总容量大小
	 * @return
	 */
	long getChannelCapacity();

	/**
	 * 
	 * @Description 组件类型
	 * @return
	 */
	String getType();

	/**
	 * 
	 * @Description Channel的使用比例
	 * @return
	 */
	double getChannelFillPercentage();
}

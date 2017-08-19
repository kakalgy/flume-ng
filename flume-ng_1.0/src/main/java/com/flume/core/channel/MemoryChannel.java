package com.flume.core.channel;

import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.core.annotations.InterfaceAudience;
import com.flume.core.annotations.InterfaceStability;
import com.flume.core.annotations.Recyclable;
import com.flume.sdk.Event;

/**
 * <p>
 * MemoryChannel is the recommended channel to use when speeds which writing to
 * disk is impractical(不现实的) is required or durability of data is not required.
 * </p>
 * <p>
 * Additionally, MemoryChannel should be used when a channel is required for
 * unit testing purposes.
 * </p>
 * 
 * @Description
 * @author Administrator
 * @date 2017年8月8日 下午8:37:11
 *
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
@Recyclable
public class MemoryChannel extends BasicChannelSemantics {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemoryChannel.class);

	/**
	 * MemroyChannel的Event容量，默认是100
	 */
	private static final Integer defaultCapacity = 100;
	/**
	 * 每个事务最大的容量，也就是每个事务能够获取的最大Event数量。默认也是100。
	 */
	private static final Integer defaultTransCapacity = 100;
	/**
	 * byteCapacitySlotSize默认100，即计算百分比的一个系数。
	 */
	private static final double byteCapacitySlotSize = 100;
	/**
	 * 首先读取配置文件定义的byteCapacity，如果没有定义，则使用默认defaultByteCapacity，而defaultByteCapacity默认是JVM物理内存的80%,
	 * 即（Runtime.getRuntime().maxMemory()*.80）；那么实际byteCapacity=定义的byteCapacity*(1-Eventheader百分比)/
	 * byteCapacitySlotSize；
	 */
	private static final Long defaultByteCapacity = (long) (Runtime.getRuntime().maxMemory() * .80);
	/*
	 * byteCapacityBufferPercentage：用来确定byteCapacity的一个百分比参数，
	 * 即我们定义的字节容量和实际事件容量的百分比，因为我们定义的字节容量主要考虑Event body，而忽略Event
	 * header，因此需要减去Event header部分的内存占用，可以认为该参数定义了Event
	 * header占了实际字节容量的百分比，默认20%；定义Channle中Event所占的百分比，需要考虑在Header中的数据。
	 */private static final Integer defaultByteCapacityBufferPercentage = 20;
	/**
	 * 定义了操作Channel Queue的等待超时事件，默认3s,增加和删除一个Event的超时时间（单位：秒）
	 */
	private static final Integer defaultKeepAlive = 3;
	
	/**
	 * 
	 * @Description
	 * @author Administrator
	 * @date   2017年8月8日 下午9:04:23
	 *
	 */
	private class MemoryTransaction extends BasicTransactionSemantics{
		/**
		 * 阻塞双端队列，从channel中取event先放入takeList，输送到sink，commit成功，从channel queue中删除
		 */
		private LinkedBlockingDeque<Event> takeList;
		/**
		 * 从source 会先放至putList，然后commit传送到channel queue队列
		 */
		private LinkedBlockingDeque<Event> putList;
		/**
		 * ChannelCounter类定义了监控指标数据的一些属性方法
		 */
		private final ChannelCounter channelCounter;
		private int putByteCounter = 0;
		private int takeByteCounter = 0;
	}

}

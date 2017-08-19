package com.flume.core.instrumentation;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

/**
 * Used for keeping track of internal metrics using atomic integers
 * </p>
 *
 * This is used by a variety of component types such as Sources, Channels,
 * Sinks, SinkProcessors, ChannelProcessors, Interceptors and Serializers.
 * 
 * @Description 定义了具体的组件的性能计数器和对应的封装方法
 * @author Administrator
 * @date 2017年8月8日 下午9:22:04
 *
 */
public abstract class MonitoredCounterGroup {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoredCounterGroup.class);

	/*
	 * Key for component's start time in
	 * MonitoredCounterGroup.counterMap,counterMap中组件开始时间的key
	 */
	private static final String COUNTER_GROUP_START_TIME = "start.time";
	/**
	 * key for component's stop time in
	 * MonitoredCounterGroup.counterMap,counterMap中组件停止时间的key
	 */
	private static final String COUNTER_GROUP_STOP_TIME = "stop.time";

	private final Type type;
	private final String name;
	private final Map<String, AtomicLong> counterMap;

	private AtomicLong startTime;
	private AtomicLong stopTime;
	private volatile boolean registered = false;

	/**
	 * 
	 * 构造函数
	 * 
	 * @param type
	 * @param name
	 * @param attrs
	 */
	public MonitoredCounterGroup(Type type, String name, String... attrs) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.name = name;

		Map<String, AtomicLong> counterInitMap = new HashMap<>();

		// Initialize the counters,初始化counterInitMap
		for (String attribute : attrs) {
			counterInitMap.put(attribute, new AtomicLong(0L));
		}

		this.counterMap = Collections.unmodifiableMap(counterInitMap);

		this.startTime = new AtomicLong(0L);
		this.stopTime = new AtomicLong(0L);
	}

	/**
	 * Starts the component
	 * <p>
	 *
	 * Initializes the values for the stop time as well as all the keys in the
	 * internal map to zero and sets the start time to the current time in
	 * milliseconds since midnight January 1, 1970 UTC
	 * 
	 * @Description 开启监控组件计数器
	 */
	public void start() {

		this.register();// 注册组件，测试用
		this.stopTime.set(0L);// 开启组件计数器，将结束时间设为0

		for (String counter : this.counterMap.keySet()) {
			this.counterMap.get(counter).set(0L);// 设置组件监测的所有项的时间为0
		}

		this.startTime.set(System.currentTimeMillis());

		LOGGER.info("Component type: " + type + ", name: " + name + " started");
	}

	/**
	 * Registers the counter. This method is exposed for testing, and there
	 * should be no need for any implementations to call this method directly.
	 * 
	 * @Description 测试时使用
	 */
	@VisibleForTesting
	void register() {

		if (!this.registered) {
			try {
				ObjectName objName = new ObjectName(
						"org.apache.flume." + this.type.name().toLowerCase(Locale.ENGLISH) + ":type=" + this.name);

				if (ManagementFactory.getPlatformMBeanServer().isRegistered(objName)) {
					LOGGER.debug("Monitored counter group for type: " + type + ", name: " + name
							+ ": Another MBean is already registered with this name. " + "Unregistering that pre-existing MBean now...");
					ManagementFactory.getPlatformMBeanServer().unregisterMBean(objName);
					LOGGER.debug("Monitored counter group for type: " + type + ", name: " + name
							+ ": Successfully unregistered pre-existing MBean.");
				}
				ManagementFactory.getPlatformMBeanServer().registerMBean(this, objName);
				LOGGER.info("Monitored counter group for type: " + type + ", name: " + name + ": Successfully registered new MBean.");

				this.registered = true;
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error("Failed to register monitored counter group for type: " + type + ", name: " + name, e);
			}
		}
	}

	/**
	 * Shuts Down the Component
	 * <p>
	 *
	 * Used to indicate that the component is shutting down.
	 * <p>
	 *
	 * Sets the stop time and then prints out the metrics from the internal map
	 * of keys to values for the following components:
	 * <p>
	 * <ul>
	 * <li>- ChannelCounter</li>
	 * <li>- ChannelProcessorCounter</li>
	 * <li>- SinkCounter</li>
	 * <li>- SinkProcessorCounter</li>
	 * <li>- SourceCounter
	 * </ul>
	 * 
	 * @Description 关闭监控计数器
	 */
	public void stop() {
		/*
		 * Sets the stopTime for the component as the current time in
		 * milliseconds
		 */
		this.stopTime.set(System.currentTimeMillis());// 设置关闭时间
		// Prints out a message indicating that this component has been stopped
		LOGGER.info("Component type: " + this.type + ", name: " + this.name + " stopped");

		// Retrieve(恢复) the type for this counter group
		final String typePrefix = this.type.name().toLowerCase(Locale.ENGLISH);

		// Print out the startTime for this component
		LOGGER.info("Shutdown Metric for type: " + this.type + ", " + "name: " + this.name + ". " + typePrefix + "."
				+ COUNTER_GROUP_START_TIME + " == " + this.startTime);

		// Print out the stopTime for this component
		LOGGER.info("Shutdown Metric for type: " + this.type + ", " + "name: " + this.name + ". " + typePrefix + "."
				+ COUNTER_GROUP_STOP_TIME + " == " + this.stopTime);

		// Retrieve and sort counter group map keys
		final List<String> mapKeys = new ArrayList<>(this.counterMap.keySet());
		Collections.sort(mapKeys);// 排序
		
		for(final String conterMapKey : mapKeys){
			final long counterMapValue = this.
		}

	}

	public static enum Type {
		SOURCE, CHANNEL_PROCESSOR, CHANNEL, SINK_PROCESSOR, SINK, INTERCEPTOR, SERIALIZER, OTHER
	}
	
	/***
}

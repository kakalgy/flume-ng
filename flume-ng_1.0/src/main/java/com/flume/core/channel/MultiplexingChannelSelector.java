package com.flume.core.channel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.core.Channel;
import com.flume.sdk.Event;
import com.flume.sdk.FlumeException;

/**
 * MultiplexingChannelSelector可以根据Source传递过来的事件流的属性来选择相应的下游Channel。
 * MultiplexingChannelSelector的配置例如下面这个例子
 * <p>
 * 1. 根据Event的Header里配置的<State, Value>属性
 * <p>
 * 2. 假如State属性的值是CA，那么这个Event流入mem-channel-1, 如果是AZ，流入file-channel-2,如果是NY,
 * 流入mem-channel-1和file-channel-2。其他的值（包含null）都流入到mem-channel-1
 * <p>
 * agent_foo.sources.avro-AppSrv-source1.selector.type = multiplexing
 * <p>
 * agent_foo.sources.avro-AppSrv-source1.selector.header = State
 * <p>
 * agent_foo.sources.avro-AppSrv-source1.selector.mapping.CA = mem-channel-1
 * <p>
 * agent_foo.sources.avro-AppSrv-source1.selector.mapping.AZ = file-channel-2
 * <p>
 * agent_foo.sources.avro-AppSrv-source1.selector.mapping.NY = mem-channel-1
 * file-channel-2
 * <p>
 * agent_foo.sources.avro-AppSrv-source1.selector.default = mem-channel-1
 * <p>
 * 
 * http://blog.csdn.net/qianshangding0708/article/details/49738415
 * 
 * @author Administrator
 *
 */
public class MultiplexingChannelSelector extends AbstractChannelSelector {

	private static final Logger LOG = LoggerFactory.getLogger(MultiplexingChannelSelector.class);

	public static final String CONFIG_MULTIPLEX_HEADER_NAME = "header";
	public static final String DEFAULT_MULTIPLEX_HEADER = "flume.selector.header";
	public static final String CONFIG_PREFIX_MAPPING = "mapping.";
	public static final String CONFIG_DEFAULT_CHANNEL = "default";
	public static final String CONFIG_PREFIX_OPTIONAL = "optional";

	private static final List<Channel> EMPTY_LIST = Collections.emptyList();

	private String headerName;

	private Map<String, List<Channel>> channelMapping;
	private Map<String, List<Channel>> optionalChannels;
	private List<Channel> defaultChannels;

	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub
		this.headerName = context.getString(CONFIG_MULTIPLEX_HEADER_NAME, DEFAULT_MULTIPLEX_HEADER);// 获取Header的值

		Map<String, Channel> channelNameMap = this.getChannelNameMap();

		this.defaultChannels = this.getChannelListFromNames(context.getString(CONFIG_DEFAULT_CHANNEL), channelNameMap);// 获取默认的Channel

		Map<String, String> mapConfig = context.getSubProperties(CONFIG_PREFIX_MAPPING);// 获取Mapping的值
		this.channelMapping = new HashMap<>();// channelMapping变量存放了header变量中必须的Channel列表
		// 将header对应的Channels存放到channelMapping变量中。
		for (String headerValue : mapConfig.keySet()) {
			List<Channel> configuredChannels = this.getChannelListFromNames(mapConfig.get(headerValue), channelNameMap);

			// This should not go to default channel(s)
			// because this seems to be a bad way to configure.
			if (configuredChannels.size() == 0) {
				throw new FlumeException("No channel configured for when " + "header value is: " + headerValue);
			}

			if (this.channelMapping.put(headerValue, configuredChannels) != null) {
				throw new FlumeException("Selector channel configured twice");
			}
		}

		// If no mapping is configured, it is ok.
		// All events will go to the default channel(s).
		Map<String, String> optionalChannelsMapping = context.getSubProperties(CONFIG_PREFIX_OPTIONAL + ".");

		this.optionalChannels = new HashMap<>();
		for (String hdr : optionalChannelsMapping.keySet()) {
			List<Channel> confChannels = this.getChannelListFromNames(optionalChannelsMapping.get(hdr), channelNameMap);
			if (confChannels.isEmpty()) {
				confChannels = EMPTY_LIST;
			}

			// Remove channels from optional channels, which are already
			// configured to be required channels.
			List<Channel> reqdChannels = this.channelMapping.get(hdr);// 在必须的channelMapping中查找出optional的Channel
			// Check if there are required channels, else defaults to default
			// channels
			if (reqdChannels == null || reqdChannels.isEmpty()) {
				reqdChannels = defaultChannels;
			}

			//// 如果header对应的Channel是必选的，那么就在optional的列表中删除
			for (Channel c : reqdChannels) {
				if (confChannels.contains(c)) {
					confChannels.remove(c);
				}
			}

			if (optionalChannels.put(hdr, confChannels) != null) {
				throw new FlumeException("Selector channel configured twice");
			}
		}
	}

	@Override
	public List<Channel> getRequiredChannels(Event event) {
		// TODO Auto-generated method stub
		String headerValue = event.getHeaders().get(this.headerName);

		if (headerValue == null || headerValue.trim().length() == 0) {
			return this.defaultChannels;
		}

		List<Channel> channels = this.channelMapping.get(headerValue);

		// This header value does not point to anything
		// Return default channel(s) here.
		if (channels == null) {
			channels = this.defaultChannels;
		}

		return channels;
	}

	@Override
	public List<Channel> getOptionalChannels(Event event) {
		// TODO Auto-generated method stub
		String hdr = event.getHeaders().get(this.headerName);

		List<Channel> channels = this.optionalChannels.get(hdr);

		if (channels == null) {
			channels = EMPTY_LIST;
		}

		return channels;
	}
}

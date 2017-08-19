package com.flume.core.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flume.core.Channel;
import com.flume.core.ChannelSelector;
import com.flume.sdk.FlumeException;

public abstract class AbstractChannelSelector implements ChannelSelector {

	private List<Channel> channels;
	private String name;

	@Override
	public List<Channel> getAllChannels() {
		// TODO Auto-generated method stub
		return this.channels;
	}

	@Override
	public void setChannels(List<Channel> channels) {
		// TODO Auto-generated method stub
		this.channels = channels;
	}

	@Override
	public synchronized void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public synchronized String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	/**
	 * 返回此ChannelSelector的所有Channel，key为Channel的name，value是Channel本身
	 * 
	 * @return A map of name to channel instance.
	 */
	protected Map<String, Channel> getChannelNameMap() {
		Map<String, Channel> channelNameMap = new HashMap<>();

		for (Channel ch : this.getAllChannels()) {
			channelNameMap.put(ch.getName(), ch);
		}
		return channelNameMap;
	}

	/**
	 * Given a list of channel names as space delimited string, returns list of
	 * channels.
	 * <p>
	 * 参数channels是一个channel列表的字符串表示，每个channel之间以空格相隔；参数channelNameMap是一个以channel的name为key，channel本身为value的map；
	 * 通过对channels的拆分和遍历，若在channelNameMap中存成对应name的Channel，则加入返回值List中
	 * 
	 * @return List of {@linkplain Channel}s represented by the names.
	 */
	protected List<Channel> getChannelListFromNames(String channels, Map<String, Channel> channelNameMap) {
		List<Channel> configuredChannels = new ArrayList<>();
		if (channels == null || channels.isEmpty()) {
			return configuredChannels;
		}
		String[] chNames = channels.split(" ");
		for (String name : chNames) {
			Channel ch = channelNameMap.get(name);
			if (ch != null) {
				configuredChannels.add(ch);
			} else {
				throw new FlumeException("Selector channel not found: " + name);
			}
		}

		return configuredChannels;
	}
}

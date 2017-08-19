package com.flume.core.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flume.configuration.Context;
import com.flume.core.Channel;
import com.flume.sdk.Event;

/**
 * ReplicatingChannelSelector,
 * 默认的ChannelSelector，复制Selector，复制就是把Source中传递过来的Event复制给所有对应的下游的Channel
 * <p>
 * ReplicatingChannelSelector会把Source传递的事件流复制给下游所有的Channel。它维护了一个requiredChannels和optionalChannels列表。
 * 在配置的optional里出现的Channel会加入到optionalChannel列表，其他的都进入requiredChannels。
 * <p>
 * Replicating channel selector. This selector allows the event to be placed in
 * all the channels that the source is configured with.
 */
public class ReplicatingChannelSelector extends AbstractChannelSelector {
	/**
	 * Configuration to set a subset of the channels as optional.
	 */
	public static final String CONFIG_OPTIONAL = "optional";

	List<Channel> requiredChannels = null;
	List<Channel> optionalChannels = new ArrayList<>();

	/**
	 * 通过对配置文件的读取，将Channel分别放入requiredChannels和optionalChannels中（默认的Channel都是requiredChannel）
	 */
	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub
		// 获取哪些Channel标记为可选
		String optionalList = context.getString(CONFIG_OPTIONAL);
		// 将所有Channel都方法必须的Channel列表中
		this.requiredChannels = new ArrayList<>(this.getAllChannels());

		Map<String, Channel> channelNameMap = this.getChannelNameMap();
		if (optionalList != null && !optionalList.isEmpty()) {
			// 下面的操作：如果channel属于可选的，则加入可选的列表中，并从必选的列表中删除
			for (String optional : optionalList.split("\\s+")) {
				Channel optionalChannel = channelNameMap.get(optional);
				this.requiredChannels.remove(optionalChannel);

				if (!this.optionalChannels.contains(optionalChannel)) {
					this.optionalChannels.add(optionalChannel);
				}
			}
		}
	}

	@Override
	public List<Channel> getRequiredChannels(Event event) {
		// TODO Auto-generated method stub
		/*
		 * Seems like there are lot of components within flume that do not call
		 * configure method. It is conceivable(可想到的) that custom component tests
		 * too do that. So in that case, revert(恢复) to old behavior.
		 */
		if (this.requiredChannels == null) {
			return this.getAllChannels();
		}
		return this.requiredChannels;
	}

	@Override
	public List<Channel> getOptionalChannels(Event event) {
		// TODO Auto-generated method stub
		return this.optionalChannels;
	}

}

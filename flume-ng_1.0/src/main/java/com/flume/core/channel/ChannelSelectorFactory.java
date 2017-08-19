package com.flume.core.channel;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.configuration.conf.BasicConfigurationConstants;
import com.flume.configuration.conf.channel.ChannelSelectorConfiguration;
import com.flume.configuration.conf.channel.ChannelSelectorType;
import com.flume.core.Channel;
import com.flume.core.ChannelSelector;
import com.flume.core.conf.Configurables;
import com.flume.sdk.FlumeException;

public class ChannelSelectorFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelSelectorFactory.class);

	/**
	 * <ul>
	 * <li>1.通过map中key为type的值，得到ChannelSelector</li>
	 * <li>2.设置ChannelSelector的channels属性</li>
	 * <li>3.通过{@link Configurables}的instanceof关键字，调用各ChannelSelector的configure方法，完成配置</li>
	 * </ul>
	 * 
	 * @param channels
	 * @param config
	 * @return
	 */
	public static ChannelSelector create(List<Channel> channels, Map<String, String> config) {
		ChannelSelector selector = getSelectorForType(config.get(BasicConfigurationConstants.CONFIG_TYPE));

		selector.setChannels(channels);

		Context context = new Context();
		context.putAll(config);

		Configurables.configure(selector, context);

		return selector;
	}

	/**
	 * <ul>
	 * <li>1.通过ChannelSelectorConfiguration中type属性，得到ChannelSelector</li>
	 * <li>2.设置ChannelSelector的channels属性</li>
	 * <li>3.通过{@link Configurables}的instanceof关键字，调用各ChannelSelector的configure方法，完成配置</li>
	 * </ul>
	 * 
	 * @param channels
	 * @param conf
	 * @return
	 */
	public static ChannelSelector create(List<Channel> channels, ChannelSelectorConfiguration conf) {
		String type = ChannelSelectorType.REPLICATING.toString();

		if (conf != null) {
			type = conf.getType();
		}

		ChannelSelector selector = getSelectorForType(type);

		selector.setChannels(channels);
		Configurables.configure(selector, conf);
		return selector;
	}

	/**
	 * 通过传入的Selector类型的字符串来返回对应的ChannelSelector实现类
	 * 
	 * @param type
	 * @return
	 */
	private static ChannelSelector getSelectorForType(String type) {
		if (type == null || type.trim().length() == 0) {
			return new ReplicatingChannelSelector();// 当type为空时，默认使用的是复制型ChannelSelector
		}

		String selectorClassName = type;
		ChannelSelectorType selectorType = ChannelSelectorType.OTHER;

		try {
			selectorType = ChannelSelectorType.valueOf(type.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			LOGGER.debug("Selector type {} is a custom type", type);

		}

		if (!selectorType.equals(ChannelSelectorType.OTHER)) {
			selectorClassName = selectorType.getChannelSelectorClassName();// 获得ChannelSelector的类路径
		}

		ChannelSelector selector = null;

		try {
			@SuppressWarnings("unchecked")
			Class<? extends ChannelSelector> selectorClass = (Class<? extends ChannelSelector>) Class.forName(selectorClassName);
			selector = selectorClass.newInstance();
		} catch (Exception e) {
			// TODO: handle exception
			throw new FlumeException("Unable to load selector type: " + type + ", class: " + selectorClassName, e);
		}

		return selector;
	}
}

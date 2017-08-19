package com.flume.core;

import com.flume.sdk.FlumeException;

public interface ChannelFactory {

	Channel create(String name, String type) throws FlumeException;

	Class<? extends Channel> getClass(String type) throws FlumeException;
}

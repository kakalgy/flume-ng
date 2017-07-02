package com.flume.configuration.conf;

import java.util.Properties;

import org.junit.Test;

import com.flume.configuration.conf.FlumeConfiguration.AgentConfiguration;

import junit.framework.Assert;

public class TestFlumeConfiguration {
	/**
	 * Test fails without FLUME-1743
	 */
	@Test
	public void testFLUME1743() throws Exception {
		Properties properties = new Properties();
		properties.put("agent1.channels", "ch0");
		properties.put("agent1.channels.ch0.type", "memory");

		properties.put("agent1.sources", "src0");
		properties.put("agent1.sources.src0.type", "multiport_syslogtcp");
		properties.put("agent1.sources.src0.channels", "ch0");
		properties.put("agent1.sources.src0.host", "localhost");
		properties.put("agent1.sources.src0.ports", "10001 10002 10003");
		properties.put("agent1.sources.src0.portHeader", "port");

		properties.put("agent1.sinks", "sink0");
		properties.put("agent1.sinks.sink0.type", "null");
		properties.put("agent1.sinks.sink0.channel", "ch0");

		FlumeConfiguration conf = new FlumeConfiguration(properties);
		AgentConfiguration agentConfiguration = conf.getConfigurationFor("agent1");
		Assert.assertEquals(String.valueOf(agentConfiguration.getSourceSet()), 1, agentConfiguration.getSourceSet().size());
		Assert.assertEquals(String.valueOf(agentConfiguration.getChannelSet()), 1, agentConfiguration.getChannelSet().size());
		Assert.assertEquals(String.valueOf(agentConfiguration.getSinkSet()), 1, agentConfiguration.getSinkSet().size());
		Assert.assertTrue(agentConfiguration.getSourceSet().contains("src0"));
		Assert.assertTrue(agentConfiguration.getChannelSet().contains("ch0"));
		Assert.assertTrue(agentConfiguration.getSinkSet().contains("sink0"));
	}
}

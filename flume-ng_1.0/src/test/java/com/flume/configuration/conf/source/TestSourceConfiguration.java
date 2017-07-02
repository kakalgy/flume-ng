package com.flume.configuration.conf.source;

import org.junit.Test;

import com.flume.configuration.Context;
import com.flume.configuration.conf.ConfigurationException;;

public class TestSourceConfiguration {
	/**
	 * Test fails without FLUME-1847
	 */
	@Test(expected = ConfigurationException.class)
	public void testFLUME1847() throws Exception {
		Context context = new Context();
		context.put("type", "something");
		SourceConfiguration sourceConfig = new SourceConfiguration("src");
		sourceConfig.configure(context);

	}
}

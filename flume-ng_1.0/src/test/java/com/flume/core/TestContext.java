package com.flume.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.flume.configuration.Context;
import com.google.common.collect.ImmutableMap;

public class TestContext {
	private Context context;

	@Before
	public void setUp() {
		context = new Context();
	}

	@Test
	public void testPutGet() {
		assertEquals("Context is empty", 0, context.getParameters().size());

		context.put("test", "value");
		assertEquals("value", context.getString("test"));
		context.clear();
		assertNull(context.getString("test"));
		assertEquals("value", context.getString("test", "value"));

		context.put("test", "true");
		assertEquals(new Boolean(true), context.getBoolean("test"));
		context.clear();
		assertNull(context.getBoolean("test"));
		assertEquals(new Boolean(true), context.getBoolean("test", true));

		context.put("test", "1");
		assertEquals(new Integer(1), context.getInteger("test"));
		context.clear();
		assertNull(context.getInteger("test"));
		assertEquals(new Integer(1), context.getInteger("test", 1));

		context.put("test", String.valueOf(Long.MAX_VALUE));
		assertEquals(new Long(Long.MAX_VALUE), context.getLong("test"));
		context.clear();
		assertNull(context.getLong("test"));
		assertEquals(new Long(Long.MAX_VALUE), context.getLong("test", Long.MAX_VALUE));

	}

	@Test
	public void testSubProperties() {
		context.put("my.key", "1");
		context.put("otherKey", "otherValue");
		assertEquals(ImmutableMap.of("key", "1"), context.getSubProperties("my."));

	}

	@Test
	public void testClear() {
		context.put("test", "1");
		context.clear();
		assertNull(context.getInteger("test"));
	}

	@Test
	public void testPutAll() {
		context.putAll(ImmutableMap.of("test", "1"));
		assertEquals("1", context.getString("test"));
	}
}

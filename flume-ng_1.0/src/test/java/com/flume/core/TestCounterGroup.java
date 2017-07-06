package com.flume.core;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCounterGroup {

	private CounterGroup counterGroup;

	@Before
	public void setUp() {
		this.counterGroup = new CounterGroup();
	}

	@Test
	public void testGetCounter() {
		AtomicLong counter = this.counterGroup.getCounter("test");

		Assert.assertNotNull(counter);
		Assert.assertEquals(0, counter.get());
	}

	@Test
	public void testGet() {
		long value = this.counterGroup.get("test");
		Assert.assertEquals(0, value);
	}

	@Test
	public void testIncrementAndGet() {
		long value = this.counterGroup.incrementAndGet("test");

		Assert.assertEquals(1, value);
	}

	@Test
	public void testAddAndGet() {
		long value = this.counterGroup.addAndGet("test", 13L);

		Assert.assertEquals(13, value);
	}

}

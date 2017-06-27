package com.flume.core;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Used for counting events, collecting metrics, etc.
 * <p>
 * 用来统计event数量，以及收集指标等
 * </p>
 * 
 * @author
 *
 */
public class CounterGroup {

	private String name;
	private HashMap<String, AtomicLong> counters;

	/**
	 * 构造函数
	 */
	public CounterGroup() {
		// TODO Auto-generated constructor stub
		counters = new HashMap<String, AtomicLong>();
	}

	public synchronized Long get(String name) {
		return this.getCounter(name).get();
	}

	public synchronized Long incrementAndGet(String name) {
		return this.getCounter(name).incrementAndGet();
	}

	public synchronized Long addAndGet(String name, Long delta) {
		return this.getCounter(name).addAndGet(delta);
	}

	public synchronized void add(CounterGroup counterGroup) {
		synchronized (counterGroup) {
			for (Entry<String, AtomicLong> entry : counterGroup.getCounters().entrySet()) {
				this.addAndGet(entry.getKey(), entry.getValue().get());
			}
		}
	}

	public synchronized void set(String name, Long value) {
		this.getCounter(name).set(value);
	}

	public synchronized AtomicLong getCounter(String name) {
		if (!counters.containsKey(name)) {
			this.counters.put(name, new AtomicLong());
		}
		return this.counters.get(name);
	}

	@Override
	public synchronized String toString() {
		// TODO Auto-generated method stub
		return "{ name:" + name + " counters:" + counters + " }";
	}

	/********************* Get/Set方法 ****************************/
	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized HashMap<String, AtomicLong> getCounters() {
		return counters;
	}

	public synchronized void setCounters(HashMap<String, AtomicLong> counters) {
		this.counters = counters;
	}

}

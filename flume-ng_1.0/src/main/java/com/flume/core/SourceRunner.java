package com.flume.core;

import com.flume.core.lifecycle.LifecycleAware;
import com.flume.core.source.EventDrivenSourceRunner;
import com.flume.core.source.PollableSourceRunner;

/**
 * A source runner controls how a source is driven.
 * 
 * <p>
 * SourceRunner用来控制 数据源是如何被使用/加载的
 *
 * This is an abstract class used for instantiating(实例化) derived(衍生) classes.
 */
public abstract class SourceRunner implements LifecycleAware {
	// 未完成
	private Source source;

	/**
	 * Static factory method to instantiate(实例化) a source runner implementation
	 * that corresponds to the type of {@link Source} specified.
	 * <p>
	 * 静态工厂方法，用来通过Source的type类型来实例化一个SourceRunner
	 * 
	 * @param source
	 *            The source to run
	 * @return A runner that can run the specified source
	 * @throws IllegalArgumentException
	 *             if the specified source does not implement a supported
	 *             derived interface of {@link SourceRunner}.
	 */
	public static SourceRunner forSource(Source source) {
		SourceRunner sourceRunner = null;

		if (source instanceof PollableSource) {
			sourceRunner = new PollableSourceRunner();
			((PollableSourceRunner) sourceRunner).setSource((PollableSource) source);
		} else {
			if (source instanceof EventDrivenSource) {
				sourceRunner = new EventDrivenSourceRunner();
				((EventDrivenSourceRunner) sourceRunner).setSource((EventDrivenSource) source);
			} else {
				throw new IllegalArgumentException("No known runner type for source " + source);
			}
		}
		return sourceRunner;
	}

	/********************************* Get/Set方法 *********************************************/
	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}
}

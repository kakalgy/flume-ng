package com.flume.core;

import com.flume.core.lifecycle.LifecycleAware;

/**
 * A source runner controls how a source is driven.
 * 
 * <p>
 * SourceRunner用来控制 数据源是如何被使用/加载的
 *
 * This is an abstract class used for instantiating(实例化) derived(衍生) classes.
 */
public abstract class SourceRunner implements LifecycleAware {

}

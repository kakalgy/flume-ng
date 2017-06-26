package com.flume.core;

import com.flume.annotations.InterfaceAudience;
import com.flume.annotations.InterfaceStability;

/**
 * Enables a component to be tagged with a name so that it can be referred to
 * uniquely within the configuration system.
 * <p>
 * 使组件能够被标记为一个名称，以便在配置系统中可以被唯一地引用
 * </p>
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface NamedComponent {

	public void setName(String name);
	
	public String getName();
}

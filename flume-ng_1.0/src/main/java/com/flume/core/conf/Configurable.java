package com.flume.core.conf;

import com.flume.annotations.InterfaceAudience;
import com.flume.annotations.InterfaceStability;
import com.flume.configuration.Context;

/**
 * <p>
 * Any class marked as Configurable may have a context including its
 * sub-configuration passed to it, requesting it configure itself.
 * </p>
 * <p>
 * 继承这个接口的类，会有一个context的配置，并且可以通过实现这个接口的方法来配置自己的配置
 * </p>
 * 
 * @author
 *
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Configurable {
	/**
	 * <p>
	 * Request the implementing class to (re)configure itself.
	 * </p>
	 * <p>
	 * When configuration parameters are changed, they must be reflected by the
	 * component asap.
	 * </p>
	 * <p>
	 * There are no thread safety guarantees on when configure might be called.
	 * </p>
	 * 
	 * @param context
	 */
	public void configure(Context context);
}

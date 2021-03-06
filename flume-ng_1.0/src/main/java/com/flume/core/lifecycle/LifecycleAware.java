package com.flume.core.lifecycle;

import com.flume.core.annotations.InterfaceAudience;
import com.flume.core.annotations.InterfaceStability;

/**
 * <p>
 * 所有核心组件都会实现org.apache.flume.lifecycle.LifecycleAware接口：
 * </p>
 * <p>
 * start方法在整个Flume启动时或者初始化组件时都会调用start方法进行组件初始化，Flume组件出现异常停止时会调用stop，getLifecycleState返回组件的生命周期状态，有IDLE,
 * START, STOP, ERROR四个状态。
 * </p>
 * 
 * <p>
 * An interface implemented by any class that has a defined, stateful,
 * lifecycle.
 * </p>
 * <p>
 * Implementations of {@link LifecycleAware} conform(符合) to a standard method of
 * starting, stopping, and reporting their current state. Additionally, this
 * interface creates a standard method of communicating failure to perform a
 * lifecycle operation to the caller (i.e. via {@link LifecycleException}). It
 * is never considered valid(有效的) to call {@link #start()} or {@link #stop()}
 * more than once or to call them in the wrong order. While this is not strictly
 * enforced, it may be in the future.
 * </p>
 * <p>
 * Example services may include Flume nodes and the master, but also lower level
 * components that can be controlled in a similar manner.
 * </p>
 * <p>
 * Example usage
 * </p>
 * 
 * <pre>
 * {@code
 * public class MyService implements LifecycleAware {
 *
 *   private LifecycleState lifecycleState;
 *
 *   public MyService() {
 *     lifecycleState = LifecycleState.IDLE;
 *   }
 *
 *   &#64;Override
 *   public void start(Context context) throws LifecycleException, InterruptedException {
 *     // ...your code does something.
 *     lifecycleState = LifecycleState.START;
 *   }
 *
 *   &#64;Override
 *   public void stop(Context context) throws LifecycleException, InterruptedException {
 *
 *     try {
 *       // ...you stop services here.
 *     } catch (SomethingException) {
 *       lifecycleState = LifecycleState.ERROR;
 *     }
 *
 *     lifecycleState = LifecycleState.STOP;
 *   }
 *
 *   &#64;Override
 *   public LifecycleState getLifecycleState() {
 *     return lifecycleState;
 *   }
 *
 * }
 * }
 * </pre>
 * 
 * @author
 *
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface LifecycleAware {
	/**
	 * <p>
	 * Starts a service or component.
	 * </p>
	 * <p>
	 * Implementations should determine the result of any start logic and effect
	 * the return value of {@link #getLifecycleState()} accordingly.
	 * </p>
	 *
	 * @throws LifecycleException
	 * @throws InterruptedException
	 */
	public void start();

	/**
	 * <p>
	 * Stops a service or component.
	 * </p>
	 * <p>
	 * Implementations should determine the result of any stop logic and effect
	 * the return value of {@link #getLifecycleState()} accordingly.
	 * </p>
	 *
	 * @throws LifecycleException
	 * @throws InterruptedException
	 */
	public void stop();

	/**
	 * <p>
	 * Return the current state of the service or component.
	 * </p>
	 */
	public LifecycleState getLifecycleState();
}

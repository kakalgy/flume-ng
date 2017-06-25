package com.flume.sdk;

/**
 * Base class of all flume exceptions.
 * <p>
 * Flume异常的基类
 * </p>
 * 
 * @author Administrator
 *
 */
public class FlumeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FlumeException(String msg) {
		super(msg);
	}

	public FlumeException(String msg, Throwable th) {
		super(msg, th);
	}

	public FlumeException(Throwable th) {
		super(th);
	}
}

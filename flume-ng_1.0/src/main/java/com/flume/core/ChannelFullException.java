package com.flume.core;

public class ChannelFullException extends ChannelException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4052606365096112901L;

	public ChannelFullException(String message) {
		// TODO Auto-generated constructor stub
		super(message);
	}

	public ChannelFullException(Throwable ex) {
		super(ex);
	}

	public ChannelFullException(String message, Throwable ex) {
		super(message, ex);
	}
}

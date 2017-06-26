package com.flume.sdk;

import java.util.Map;

/**
 * Basic representation of a data object in Flume. Provides access to data as it
 * flows through the system.
 * <p>
 * 一个数据单元，带有一个可选的消息头
 * </p>
 */
public interface Event {
	/**
	 * Returns a map of name-value pairs describing the data stored in the body.
	 */
	public Map<String, String> getHeaders();

	/**
	 * Set the event headers
	 * 
	 * @param headers
	 *            Map of headers to replace the current headers.
	 */
	public void setHeaders(Map<String, String> headers);

	/**
	 * Returns the raw byte array of the data contained in this event.
	 */
	public byte[] getBody();

	/**
	 * Sets the raw byte array of the data contained in this event.
	 * 
	 * @param body
	 *            The data.
	 */
	public void setBody(byte[] body);
}

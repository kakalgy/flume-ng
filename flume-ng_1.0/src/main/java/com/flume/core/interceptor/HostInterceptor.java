package com.flume.core.interceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flume.configuration.Context;
import com.flume.sdk.Event;

import static com.flume.core.interceptor.HostInterceptor.Constants.*;

/**
 * Simple Interceptor class that sets the host name or IP on all events that are
 * intercepted.
 * <p>
 * The host header is named <code>host</code> and its format is either the
 * FQDN((Fully Qualified Domain Name)完全合格域名/全称域名) or IP of the host on which
 * this interceptor is run.
 *
 *
 * Properties:
 * <p>
 *
 * preserveExisting: Whether to preserve an existing value for 'host' (default
 * is false)
 * <p>
 *
 * useIP: Whether to use IP address or fully-qualified hostname for 'host'
 * header value (default is true)
 * <p>
 *
 * hostHeader: Specify the key to be used in the event header map for the host
 * name. (default is "host")
 * <p>
 *
 * Sample config:
 * <p>
 *
 * <code>
 *   agent.sources.r1.channels = c1<p>
 *   agent.sources.r1.type = SEQ<p>
 *   agent.sources.r1.interceptors = i1<p>
 *   agent.sources.r1.interceptors.i1.type = host<p>
 *   agent.sources.r1.interceptors.i1.preserveExisting = true<p>
 *   agent.sources.r1.interceptors.i1.useIP = false<p>
 *   agent.sources.r1.interceptors.i1.hostHeader = hostname<p>
 * </code>
 *
 */
public class HostInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(HostInterceptor.class);

	private final boolean preserveExisting;
	private final String header;
	private String host = null;

	private HostInterceptor(boolean preserveExisting, boolean useIP, String header) {
		// TODO Auto-generated constructor stub
		this.preserveExisting = preserveExisting;
		this.header = header;

		InetAddress addr;

		try {
			addr = InetAddress.getLocalHost();
			if (useIP) {
				host = addr.getHostAddress();
			} else {
				host = addr.getCanonicalHostName();
			}

		} catch (UnknownHostException e) {
			// TODO: handle exception
			logger.warn("Could not get local host address. Exception follows.", e);
		}
	}

	public void initialize() {
		// TODO Auto-generated method stub
		// 无操作
	}

	/**
	 * Modifies events in-place.
	 */
	public Event intercept(Event event) {
		// TODO Auto-generated method stub
		Map<String, String> headers = event.getHeaders();

		if (preserveExisting && headers.containsKey(header)) {
			return event;
		}
		if (host != null) {
			headers.put(header, host);
		}
		return event;
	}

	/**
	 * Delegates to {@link #intercept(Event)} in a loop.
	 * 
	 * @param events
	 * @return
	 */
	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		for (Event event : events) {
			this.intercept(event);
		}
		return events;
	}

	public void close() {
		// TODO Auto-generated method stub
		// 无操作
	}

	/**
	 * Builder which builds new instances of the HostInterceptor.
	 */
	public static class Builder implements Interceptor.Builder {
		private boolean preserveExisting = PRESERVE_DELF;
		private boolean useIP = USE_IP_DFLT;
		private String header = HOST;

		public Interceptor build() {
			// TODO Auto-generated method stub
			return new HostInterceptor(preserveExisting, useIP, header);
		}

		public void configure(Context context) {
			// TODO Auto-generated method stub
			preserveExisting = context.getBoolean(PRESERVE, PRESERVE_DELF);
			useIP = context.getBoolean(USE_IP, USE_IP_DFLT);
			header = context.getString(HOST_HEADER, HOST);
		}
	}

	public static class Constants {
		public static String HOST = "host";
		public static String PRESERVE = "preserveExisting";
		public static boolean PRESERVE_DELF = false;

		public static String USE_IP = "useIP";
		public static boolean USE_IP_DFLT = true;

		public static String HOST_HEADER = "hostHeader";
	}
}

package org.tomstools.crawler.http;


/**
 * 代理服务
 * @author lotomer
 *
 */
public class Proxy{
	private String hostName;
	private int port;
	private String scheme;
	public Proxy(Proxy other) {
		this.hostName = other.hostName;
		this.port = other.port;
		this.scheme = other.scheme;
	}

	public Proxy() {
		super();
	}

	
	public final void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public final void setPort(int port) {
		this.port = port;
	}

	public final void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public final String getHostName() {
		return hostName;
	}

	public final int getPort() {
		return port;
	}

	public final String getScheme() {
		return scheme;
	}

}

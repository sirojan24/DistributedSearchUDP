package com.uom.cse.distsearch.model;

public class Request {
	private String host;
	private int port;
	private String message;

	public Request(String host, int port, String message) {
		super();
		this.host = host;
		this.port = port;
		this.message = message;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return host + ":" + port + " - " + message;
	}
}

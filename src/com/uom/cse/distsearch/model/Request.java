package com.uom.cse.distsearch.model;

public class Request {
	private String host;
	private int port;
	private String message;
	private int responseCode;

	public Request(String host, int port, String message) {
		super();
		this.host = host;
		this.port = port;
		this.message = message;
		
		String[] response = message.split(" ");
		try{
			responseCode = Integer.parseInt(response[2]);
		}catch(Exception e){
			
		}
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

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
}

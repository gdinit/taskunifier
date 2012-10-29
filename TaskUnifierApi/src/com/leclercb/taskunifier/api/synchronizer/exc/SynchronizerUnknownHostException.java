package com.leclercb.taskunifier.api.synchronizer.exc;

public class SynchronizerUnknownHostException extends SynchronizerException {
	
	private String host;
	
	public SynchronizerUnknownHostException(
			boolean expected,
			String host,
			String message) {
		this(expected, host, message, null);
	}
	
	public SynchronizerUnknownHostException(
			boolean expected,
			String host,
			String message,
			Throwable throwable) {
		super(expected, message, throwable);
		this.host = host;
	}
	
	public String getHost() {
		return this.host;
	}
	
}

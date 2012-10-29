package com.leclercb.taskunifier.api.synchronizer.exc;

public class SynchronizerNotConnectedException extends SynchronizerException {
	
	private String host;
	
	public SynchronizerNotConnectedException(
			boolean expected,
			String host,
			String message) {
		this(expected, host, message, null);
	}
	
	public SynchronizerNotConnectedException(
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

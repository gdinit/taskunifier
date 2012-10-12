package com.leclercb.commons.api.utils.exceptions;

public class SingleInstanceException extends Exception {
	
	public SingleInstanceException() {
		super();
	}
	
	public SingleInstanceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SingleInstanceException(String message) {
		super(message);
	}
	
	public SingleInstanceException(Throwable cause) {
		super(cause);
	}
	
}

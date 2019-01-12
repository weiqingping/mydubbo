package com.my.test.dubbo.exception;

public class NotFindInterfaceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFindInterfaceException() {
		super();
	}

	public NotFindInterfaceException(String message) {
		super(message);
	}

	public NotFindInterfaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFindInterfaceException(Throwable cause) {
		super(cause);
	}
}

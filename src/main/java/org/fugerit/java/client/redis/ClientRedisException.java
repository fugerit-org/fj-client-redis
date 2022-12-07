package org.fugerit.java.client.redis;

public class ClientRedisException extends Exception {

	private static final long serialVersionUID = -272131378873223789L;

	public ClientRedisException() {
		super();
	}

	public ClientRedisException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ClientRedisException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientRedisException(String message) {
		super(message);
	}

	public ClientRedisException(Throwable cause) {
		super(cause);
	}
	
}

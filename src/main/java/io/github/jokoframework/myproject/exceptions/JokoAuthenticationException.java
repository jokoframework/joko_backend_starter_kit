package io.github.jokoframework.myproject.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JokoAuthenticationException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JokoAuthenticationException(String message, Throwable e) {
		super(message, e);
	}

	public JokoAuthenticationException(String message) {
		super(message);
	}

	
}

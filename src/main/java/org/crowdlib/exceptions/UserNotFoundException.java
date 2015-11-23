package org.crowdlib.exceptions;

public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = -653870917875607543L;

	public UserNotFoundException(String msg) {
		super(msg);
	}

	public UserNotFoundException(String msg, Exception e) {
		super(msg, e);
	}
}

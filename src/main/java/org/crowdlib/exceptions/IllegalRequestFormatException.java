package org.crowdlib.exceptions;

public class IllegalRequestFormatException extends Exception {
	private static final long serialVersionUID = -653870917875607543L;

	private static final String msg = "Request Format is illegal";
	
	public IllegalRequestFormatException() {
		super(msg);
	}
}

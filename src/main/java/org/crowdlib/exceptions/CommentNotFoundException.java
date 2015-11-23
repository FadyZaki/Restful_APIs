package org.crowdlib.exceptions;

public class CommentNotFoundException extends Exception {

	private static final long serialVersionUID = 1808743362263658538L;

	public CommentNotFoundException(String msg) {
		super(msg);
	}

	public CommentNotFoundException(String msg, Exception e) {
		super(msg, e);
	}
}

package org.crowdlib.exceptions;

public class CatalogueItemNotFoundException extends Exception {

	private static final long serialVersionUID = 6274846720511918832L;

	public CatalogueItemNotFoundException(String msg) {
		super(msg);
	}

	public CatalogueItemNotFoundException(String msg, Exception e) {
		super(msg, e);
	}
}

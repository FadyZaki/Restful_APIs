package org.crowdlib.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class CustomizedWebApplicationException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public CustomizedWebApplicationException(Status status, String message) {
		super(Response.status(status).type(MediaType.TEXT_PLAIN).entity("Error : " + message).build());
	}
	
	
}

package org.crowdlib.main;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.exceptions.UserNotFoundException;
import org.crowdlib.inmemory.collections.InMemoryUserCollection;
import org.crowdlib.model.UserDao;
import org.crowdlib.model.UserDaoImpl;
import org.glassfish.jersey.internal.util.Base64;

/**
 * Simple request filter to implement basic authentication on the basis of an
 * in-memory set of user credentials that are statically defined. You may want
 * to extend this.
 *
 * For information on filters see:
 * https://jersey.java.net/documentation/latest/filters-and-interceptors.html
 *
 * @author Alex Voss - alex.voss@st-andrews.ac.uk
 */
@PreMatching
public class AuthFilter implements ContainerRequestFilter {

	private UserDao userDao;
	
	public AuthFilter() {
		this.userDao = new UserDaoImpl();
	}

	// Exception thrown if user is unauthorized.
	private static final WebApplicationException UNAUHTORISED = new WebApplicationException(
			Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"realm\"")
					.entity("Page requires login.").build());

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the authentication passed in HTTP headers parameters
		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authHeader == null) {
			throw UNAUHTORISED;
		}

		authHeader = authHeader.replaceFirst("[Bb]asic ", "");
		final String userCreds = Base64.decodeAsString(authHeader);

		User user = InMemoryUserCollection.getUser(userCreds.split(":")[0]);

		if (user == null)
			throw UNAUHTORISED;

		final String credential = user.getUsername() + ":" + user.getPassword();
		if (userCreds.equals(credential)) {
			final SecurityContext sc = new MySecurityContext(user.getUsername());
			requestContext.setSecurityContext(sc);
			return;
		}

		throw UNAUHTORISED;
	}

	/**
	 * A simple implementation of a {@link SecurityContext} that is just enough
	 * to implement HTTP basic authentication.
	 */
	private class MySecurityContext implements SecurityContext {

		private Principal principal = null;

		/**
		 * Constructor takes the name of the authenticated users as its
		 * argument, which will be returned in a {@link MyUserPrincipal} object
		 * by the {@link #getUserPrincipal()} method.
		 */
		public MySecurityContext(final String principalName) {
			this.principal = new MyUserPrincipal(principalName);
		}

		@Override
		public Principal getUserPrincipal() {
			return this.principal;
		}

		@Override
		public boolean isUserInRole(final String role) {
			User currentUser;
			try {
				currentUser = userDao.getUserByUsername(principal.getName());
				return currentUser.getRole().equals(role);
			} catch (UserNotFoundException e) {
				throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
			}
		}

		@Override
		public boolean isSecure() {
			return false;
		}

		@Override
		public String getAuthenticationScheme() {
			return SecurityContext.BASIC_AUTH;
		}
	}

	/**
	 * Simple implementation of {@link Principal}, simply stores a username.
	 */
	private class MyUserPrincipal implements Principal {

		private String name = null;

		/**
		 * Constuctor taking a username as the argument.
		 */
		public MyUserPrincipal(final String principalName) {
			this.name = principalName;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}

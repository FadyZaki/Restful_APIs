package org.crowdlib.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Example of a client implementation.
 */
public final class MyResourceClient {

    /**
     * Private constructor, this is a utility class.
     */
    private MyResourceClient() {

    }

    /**
     * Main method makes a single authenticated request.
     */
    public static void main(final String[] args) {
        final Client client = ClientBuilder.newClient();
        client.register(HttpAuthenticationFeature.basic("student1", "whoopey"));
        final WebTarget target = client.target("http://localhost:9998").path("myresource");
        final String response = target.request().get(String.class);
        System.err.println(response);
    }

}

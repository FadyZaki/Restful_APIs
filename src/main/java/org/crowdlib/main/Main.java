package org.crowdlib.main;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.exceptions.mappers.NotFoundExceptionMapper;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.crowdlib.inmemory.collections.InMemoryCommentCollection;
import org.crowdlib.inmemory.collections.InMemoryUserCollection;
import org.crowdlib.webservices.api.CatalogueItemResource;
import org.crowdlib.webservices.api.CommentResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 * Main application class. Contains code to run the RESTful service in a Grizzly container.
 */
public final class Main {

    public static final URI BASE_URI = URI.create("http://localhost:9998/");

    /**
     * Private constructor - this is a utility class.
     */
    private Main() {
    };

    /**
     * Create a Grizzly server and register the classes that make up this application.
     */
    protected static HttpServer createServer() throws IOException {
        final ResourceConfig rc = new ResourceConfig();
        rc.packages("org.crowdlib.main");
        rc.packages("org.crowdlib.webservices.api");
        rc.packages("org.crowdlib.exceptions");
        rc.packages("org.crowdlib.exceptions.mappers");
        rc.register(MyResource.class);
        rc.register(CommentResource.class);
        rc.register(CatalogueItemResource.class);
        rc.register(RolesAllowedDynamicFeature.class);
        rc.register(AuthFilter.class);
        rc.register(NotFoundExceptionMapper.class);
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }
    
    private static void initializeInMemoryCollections(){
    	InMemoryUserCollection.initializeInMemoryUsers();
    	InMemoryCatalogueItemCollection.initializeInMemoryCatalogueItems();
    	InMemoryCommentCollection.initializeInMemoryComments();
    }

    /**
     * main() method starts up Grizzly server, waits for user input, then shuts it down.
     */
    public static void main(final String[] args) throws IOException {
        initializeInMemoryCollections();
    	final HttpServer httpServer = createServer();
        System.out.println("Starting grizzly2...");
        httpServer.start();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl%nHit enter to stop it...", BASE_URI));
        System.in.read();
        httpServer.shutdownNow();
    }
}

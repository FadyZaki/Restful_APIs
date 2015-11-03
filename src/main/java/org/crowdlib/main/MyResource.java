package org.crowdlib.main;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * A simple example of a RESTful service.
 */
@Path("/myresource")
public class MyResource {

    @Context
    private SecurityContext securityContext;
    
    public void setSecurityContext(SecurityContext securityContext) {
      this.securityContext = securityContext;
    }

    /**
     * Get a simple greeting.
     */
    @GET
    @Produces("text/plain")
    public final String getIt() {     
        return "Got it! Thanks, " + securityContext.getUserPrincipal().getName();
    }
}

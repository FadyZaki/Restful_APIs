package org.crowdlib.webservices.api;

import java.net.URI;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.model.CatalogueItemDao;
import org.crowdlib.model.CatalogueItemDaoImpl;
import org.crowdlib.model.CommentDao;
import org.crowdlib.model.CommentDaoImpl;
import org.crowdlib.model.UserDao;
import org.crowdlib.model.UserDaoImpl;

@Path("/items")
@PermitAll
public class CatalogueItemResource {

	private CatalogueItemDao catalogueItemDao;
	
	@Context
	private UriInfo uriInfo;
	
	@Context   
	ResourceContext rc;

	public CatalogueItemResource() {
		this.catalogueItemDao = new CatalogueItemDaoImpl();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CatalogueItem> getCatalogueItems() {
		return catalogueItemDao.getAll();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{itemId}")
	public CatalogueItem getCatalogueItem(@PathParam("itemId") Integer itemId) {
		CatalogueItem item;
		try {
			item = catalogueItemDao.getById(itemId);
			return item;
		} catch (CatalogueItemNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
	}

	@Path("/{itemId}/comments")
	public CommentResource getCommentResource() {
		return rc.getResource(CommentResource.class);
	}

}

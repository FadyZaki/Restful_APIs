package org.crowdlib.webservices.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.crowdlib.daos.CatalogueItemDao;
import org.crowdlib.daos.CatalogueItemDaoImpl;
import org.crowdlib.daos.CommentDao;
import org.crowdlib.daos.CommentDaoImpl;
import org.crowdlib.daos.UserDao;
import org.crowdlib.daos.UserDaoImpl;
import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.Notification;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CommentNotFoundException;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.exceptions.UserNotFoundException;

@Path("/users")
public class UserResource {

	private CatalogueItemDao catalogueItemDao;
	private UserDao userDao;
	private CommentDao commentDao;
	@Context
	private SecurityContext securityContext;

	public UserResource() {
		this.catalogueItemDao = new CatalogueItemDaoImpl();
		this.userDao = new UserDaoImpl();
		this.commentDao = new CommentDaoImpl();
	}

	@GET
	@Path("/self")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser() {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
		} catch (UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
		return currentUser;
	}

	@GET
	@Path("/self/favourites")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getUserFavouriteComments() {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			if(currentUser == null) throw new CustomizedWebApplicationException(Status.NOT_FOUND, "User not found");
		} catch (UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
		return userDao.getUserFavouriteComments(currentUser);
	}

	@PUT
	@Path("/self/favourites/{commentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Comment addCommentToUserFavourites(@PathParam("commentId") int commentId) {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			Comment favouriteComment = commentDao.getById(commentId);
			if (!userDao.checkIfCommentIsAmongFavourites(currentUser, favouriteComment)) {
				userDao.addCommentToFavourites(currentUser, favouriteComment);
				commentDao.incrementFavouritesCount(favouriteComment);
			}
			return favouriteComment;
		} catch (UserNotFoundException | CommentNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}

	}
	
	@GET
	@Path("/self/followedItems")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CatalogueItem> getUserFollowedItems() {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			return userDao.getUserFollowedItems(currentUser);
		} catch (UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
	}
	
	@PUT
	@Path("/self/followedItems/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItemToUserFollowedItems(@PathParam("itemId") int itemId) {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			CatalogueItem item = catalogueItemDao.getById(itemId);
			if (!userDao.checkIfItemIsAmongFavourites(currentUser, item)) {
				userDao.addItemToFollowedItems(currentUser, item);
				catalogueItemDao.addFollower(item, currentUser);
			}
			return Response.ok().entity(item).build();
		} catch (UserNotFoundException | CatalogueItemNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
	}
	
	@GET
	@Path("/self/notifications")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Notification> getUserNotifications() {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			return userDao.getListOfNotifications(currentUser);
		} catch (UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
	}

}

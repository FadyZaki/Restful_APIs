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

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.Notification;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CommentNotFoundException;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.exceptions.UserNotFoundException;
import org.crowdlib.model.CatalogueItemDao;
import org.crowdlib.model.CatalogueItemDaoImpl;
import org.crowdlib.model.CommentDao;
import org.crowdlib.model.CommentDaoImpl;
import org.crowdlib.model.UserDao;
import org.crowdlib.model.UserDaoImpl;

/**
 * 
 * @author Fz20
 *This Class represents the User web service providing all services related to users
 */
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

	public void setSecurityContext(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	/**
	 * Get The current logged in user
	 * @return current logged in user
	 */
	@GET
	@Path("/self")
	@Produces(MediaType.APPLICATION_JSON)
	public User getCurrentLoggedUser() {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
		} catch (UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
		return currentUser;
	}

	/**
	 * Retrieves a list of the user's favourite comments
	 * @return list of user's favourite comments
	 */
	@GET
	@Path("/self/favourites")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getUserFavouriteComments() {
		User currentUser;

		currentUser = getCurrentLoggedUser();

		return userDao.getUserFavouriteComments(currentUser);
	}

	/**
	 * Adds a comment to the list of user favourite comments
	 * @param commentId the id of the comment to be added to the list of favourites
	 * @return Comment added to the list of favourites
	 */
	@PUT
	@Path("/self/favourites/{commentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Comment addCommentToUserFavourites(@PathParam("commentId") int commentId) {
		User currentUser;
		try {
			currentUser = getCurrentLoggedUser();
			Comment favouriteComment = commentDao.getById(commentId);
			if (!userDao.checkIfCommentIsAmongFavourites(currentUser, favouriteComment)) {
				userDao.addCommentToFavourites(currentUser, favouriteComment);
				commentDao.incrementFavouritesCount(favouriteComment);
			}
			return favouriteComment;
		} catch (CommentNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}

	}

	/**
	 * Retrives a list of the items that the user is currently following
	 * @return List of items followed by the user
	 */
	@GET
	@Path("/self/followedItems")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CatalogueItem> getUserFollowedItems() {
		User currentUser;
		currentUser = getCurrentLoggedUser();
		return userDao.getUserFollowedItems(currentUser);
	}

	/**
	 * Adds an item to the user followed catalogue items.
	 * @param itemId The id of the item to be added
	 * @return a Response object containing an entity representing the item
	 */
	@PUT
	@Path("/self/followedItems/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItemToUserFollowedItems(@PathParam("itemId") int itemId) {
		User currentUser;
		try {
			currentUser = getCurrentLoggedUser();
			CatalogueItem item = catalogueItemDao.getById(itemId);
			if (!userDao.checkIfItemIsAmongFollowedItems(currentUser, item)) {
				userDao.addItemToFollowedItems(currentUser, item);
				catalogueItemDao.addFollower(item, currentUser);
			}
			return Response.ok().entity(item).build();
		} catch (CatalogueItemNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Retrieves all the user's notifications
	 * @return a list of the user's notifications
	 */
	@GET
	@Path("/self/notifications")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Notification> getUserNotifications() {
		User currentUser;
		currentUser = getCurrentLoggedUser();
		return userDao.getListOfNotifications(currentUser);

	}

}

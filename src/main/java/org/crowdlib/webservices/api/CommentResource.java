package org.crowdlib.webservices.api;

import java.net.URI;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.crowdlib.constants.ExplanatoryMessagesConstants;
import org.crowdlib.constants.RoleTypeConstants;
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
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.crowdlib.inmemory.collections.InMemoryUserCollection;

@Path("/")
@PermitAll
public class CommentResource {

	private CatalogueItemDao catalogueItemDao;
	private UserDao userDao;
	private CommentDao commentDao;

	@Context
	private SecurityContext securityContext;

	@Context
	private UriInfo uriInfo;
	

	public CommentResource() {
		this.catalogueItemDao = new CatalogueItemDaoImpl();
		this.userDao = new UserDaoImpl();
		this.commentDao = new CommentDaoImpl();
	}
	
	public void setSecurityContext(SecurityContext securityContext){
		this.securityContext = securityContext;
	}
	
	public void setUriInfo(UriInfo uriInfo){
		this.uriInfo = uriInfo;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCatalogueItemComments(@PathParam("itemId") Integer itemId, @QueryParam("start") Integer start,
			@QueryParam("size") Integer size) {
		CatalogueItem currentCatalogueItem;
		try {
			currentCatalogueItem = catalogueItemDao.getById(itemId);
			User currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			List<Comment> comments;
			Response response;
			if (start != null && size != null) {
				comments = catalogueItemDao.getSubsetOfComments(currentCatalogueItem, start, size);
				int newStart = adjustStartIndexForPagination(start, size, currentCatalogueItem);
				response = Response.ok().entity(comments).link(uriInfo.getAbsolutePathBuilder()
						.replaceQueryParam("start", newStart).replaceQueryParam("size", size).build(), "Next Page").build();
			} else {
				comments = catalogueItemDao.getAllComments(currentCatalogueItem);
				response = Response.ok().entity(comments).build();
			}
			userDao.adjustListOfNotificationsAfterAListOfCommentsIsSeen(currentUser, comments);
			return response;
		} catch (CatalogueItemNotFoundException | UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}

	}

	private int adjustStartIndexForPagination(Integer start, Integer size, CatalogueItem currentCatalogueItem) {
		int numberOfRemainingComments = catalogueItemDao.getNumberOfRemainingComments(currentCatalogueItem,
				start + size);
		int newStart;
		if (numberOfRemainingComments > 0) {
			newStart = start + size;
		} else {
			newStart = 0;
		}
		return newStart;
	}

	@Path("/{commentId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Comment getComment(@PathParam("commentId") Integer commentId) {
		Comment comment;
		try {
			comment = commentDao.getById(commentId);
			User currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			userDao.adjustListOfNotificationsAfterCommentIsSeen(currentUser, comment);
			return comment;
		} catch (CommentNotFoundException | UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public Comment addCommentToCatalogueItem(@PathParam("itemId") Integer itemId, String commentContent) {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			CatalogueItem currentCatalogueItem = catalogueItemDao.getById(itemId);
			Comment comment = commentDao.createComment(commentContent, currentUser, currentCatalogueItem);
			URI uriToComment = getUriToComment(uriInfo, comment);
			commentDao.adjustLinkToSelf(comment, Link.fromUri(uriToComment).rel("self").build());
			commentDao.adjustLinkToReplies(comment,
					Link.fromUri(UriBuilder.fromUri(uriToComment).path("replies").build()).rel("replies").build());
			Link linkToComment = Link.fromUri(uriToComment).rel("comment").build();
			catalogueItemDao.addComment(currentCatalogueItem, comment, linkToComment);
			List<User> followers = catalogueItemDao.getAllFollowers(currentCatalogueItem);
			for (User follower : followers) {
				userDao.addToListOfNotifications(follower, new Notification(currentCatalogueItem, comment, linkToComment));
			}
			return comment;
		} catch (UserNotFoundException | CatalogueItemNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}

	}

	@Path("/{commentId}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public Comment addReplyToComment(@PathParam("commentId") Integer commentId,
			String replyContent) {
		User currentUser;
		try {
			currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			Comment comment = commentDao.getById(commentId);
			Comment reply = commentDao.createComment(replyContent, currentUser, comment.getCatalogueItem());
			commentDao.addReply(comment, reply);
			URI uriToReply = getUriToReply(uriInfo, reply);
			commentDao.adjustLinkToSelf(reply, Link.fromUri(uriToReply).rel("self").build());
			commentDao.adjustLinkToReplies(reply,
					Link.fromUri(UriBuilder.fromUri(uriToReply).path("replies").build()).rel("replies").build());
			return reply;
		} catch (UserNotFoundException | CommentNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
		
	}

	@Path("/{commentId}/replies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getCommentReplies(@PathParam("commentId") Integer commentId) {
		Comment comment;
		try {
			comment = commentDao.getById(commentId);
			return commentDao.getReplies(comment);
		} catch (CommentNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}
	}

	@Path("/{commentId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Comment deleteComment(@PathParam("commentId") Integer commentId) {
		Comment comment;
		try {
			comment = commentDao.getById(commentId);
			User currentUser = userDao.getUserByUsername(securityContext.getUserPrincipal().getName());
			if (commentDao.isCommentOwner(currentUser, comment)) {
				commentDao.deleteComment(comment, ExplanatoryMessagesConstants.OWNER_DELETION_MESSAGE);
			} else if (securityContext.isUserInRole(RoleTypeConstants.ADMIN_USER)) {
				commentDao.deleteComment(comment, ExplanatoryMessagesConstants.ADMIN_DELETION_MESSAGE);
			}
			return comment;
		} catch (CommentNotFoundException | UserNotFoundException e) {
			throw new CustomizedWebApplicationException(Status.NOT_FOUND, e.getMessage());
		}

	}

	private URI getUriToComment(UriInfo uriInfo, Comment comment) {
		URI locationURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(comment.getId())).build();
		return locationURI;
	}

	private URI getUriToReply(UriInfo uriInfo, Comment reply) {
		String absolutePath = uriInfo.getAbsolutePath().toString();
		String pathToComments = absolutePath.substring(0, absolutePath.lastIndexOf('/'));
		return UriBuilder.fromPath(pathToComments).path(String.valueOf(reply.getId())).build();
	}

}

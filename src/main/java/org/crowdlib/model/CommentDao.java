package org.crowdlib.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CommentNotFoundException;

public interface CommentDao {

	/**
	 * Retrieves comment by id
	 * @param commentId Id of comment
	 * @return Comment specified by id
	 * @throws CommentNotFoundException if comment is not available in memory
	 */
	public Comment getById(Integer commentId) throws CommentNotFoundException;
	
	/**
	 * Creates a new comment using the specified arguments
	 * @param commentContent Content for the new Comment
	 * @param commentOwner Owner of this comment
	 * @param item Item to which this comment belongs
	 * @return Comment created
	 */
	public Comment createComment(String commentContent, User commentOwner, CatalogueItem item);
	
	/**
	 * Retrieves all the replies for the specified comment
	 * @param comment Comment specified
	 * @return List of replies for this comment
	 */
	public List<Comment> getReplies(Comment comment);
	
	/**
	 * Adds a reply to the specified comment
	 * @param comment Comment To which the reply is added
	 * @param reply Reply to be added
	 */
	public void addReply(Comment comment, Comment reply);
	
	/**
	 * sets a link to the comment itself
	 * @param comment Comment specified
	 * @param linkToSelf
	 */
	public void adjustLinkToSelf(Comment comment, Link linkToSelf);
	
	/**
	 * Sets a link for the comment replies
	 * @param comment Comment specified
	 * @param linkToReplies Link to replies for this comment
	 */
	public void adjustLinkToReplies(Comment comment, Link linkToReplies);
	
	/**
	 * Increment number of favourites for this comment
	 * @param comment comment specified
	 */
	public void incrementFavouritesCount(Comment comment);
	
	/**
	 * Checks if the specified user is the owner of this comment
	 * @param user specified user
	 * @param comment specified comment
	 * @return true if user is the owner
	 */
	public boolean isCommentOwner(User user, Comment comment);
	
	/**
	 * Delete the comment specified by changing its content to the deletion message
	 * and deleting its replies
	 * @param comment Comment to be deleted
	 * @param deletionMessage Deletion message to be placed as comment content
	 */
	public void deleteComment(Comment comment, String deletionMessage);
}

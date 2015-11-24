package org.crowdlib.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CommentNotFoundException;

public interface CommentDao {

	public Comment getById(Integer commentId) throws CommentNotFoundException;
	
	public Comment createComment(String commentContent, User commentOwner, CatalogueItem item);
	
	public List<Comment> getReplies(Comment comment);
	
	public void addReply(Comment comment, Comment reply);
	
	public void adjustLinkToSelf(Comment comment, Link linkToSelf);
	
	public void adjustLinkToReplies(Comment comment, Link linkToReplies);
	
	public void incrementFavouritesCount(Comment comment);
	
	public boolean isCommentOwner(User user, Comment comment);
	
	public void deleteComment(Comment comment, String deletionMessage);
}

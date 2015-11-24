package org.crowdlib.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CommentNotFoundException;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.crowdlib.inmemory.collections.InMemoryCommentCollection;

public class CommentDaoImpl implements CommentDao {

	@Override
	public Comment getById(Integer commentId) throws CommentNotFoundException {
		Comment comment = InMemoryCommentCollection.getComment(commentId);
		if(comment == null) throw new CommentNotFoundException("Comment not available");
		return comment;
	}
	
	@Override
	public Comment createComment(String commentContent, User commentOwner, CatalogueItem item) {
		Comment comment = new Comment(commentContent,commentOwner, item);
		InMemoryCommentCollection.addComment(comment);
		return comment;
	}

	@Override
	public void addReply(Comment comment, Comment reply) {
		InMemoryCommentCollection.addComment(reply);
		reply.setParentComment(comment);
		comment.getReplies().add(reply);
	}

	@Override
	public void adjustLinkToSelf(Comment comment, Link linkToSelf) {
		comment.setLinkToSelf(linkToSelf);
	}
	
	@Override
	public void adjustLinkToReplies(Comment comment, Link linkToReplies) {
		comment.setLinkToReplies(linkToReplies);
	}

	@Override
	public void incrementFavouritesCount(Comment comment) {
		int favouritesCounts = comment.getFavouritesCount()+1;
		comment.setFavouritesCount(favouritesCounts);
	}

	@Override
	public List<Comment> getReplies(Comment comment) {
		return comment.getReplies();
	}

	@Override
	public boolean isCommentOwner(User user, Comment comment) {
		return comment.getOwner().equals(user);
	}

	@Override
	public void deleteComment(Comment comment, String deletionMessage) {
		comment.setCommentContent(deletionMessage);
		comment.getReplies().clear();
		comment.setLinkToReplies(null);
	}

	

}

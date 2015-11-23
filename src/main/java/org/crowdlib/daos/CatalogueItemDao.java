package org.crowdlib.daos;

import java.util.List;

import javax.ws.rs.core.Link;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CommentNotFoundException;

public interface CatalogueItemDao {

	public List<CatalogueItem> getAll();
	
	public CatalogueItem getById(Integer itemId) throws CatalogueItemNotFoundException;
	
	public List<Comment> getAllComments(CatalogueItem item);
	
	public List<Comment> getSubsetOfComments(CatalogueItem item, Integer startIndex, Integer size);

	public void addComment(CatalogueItem item, Comment comment, Link linkToComment);

	public int getNumberOfRemainingComments(CatalogueItem currentCatalogueItem, Integer start);
	
	public void addFollower(CatalogueItem item, User follower);
	
	public List<User> getAllFollowers(CatalogueItem item);

}

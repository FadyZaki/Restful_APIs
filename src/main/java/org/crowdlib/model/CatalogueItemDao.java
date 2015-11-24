package org.crowdlib.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CommentNotFoundException;

public interface CatalogueItemDao {

	/**
	 * Retrieve a list of all catalogue Items in memory
	 * @return List of catalogue items in memory
	 */
	public List<CatalogueItem> getAll();
	
	/**
	 * Retrieves the catalogue item specified by this id
	 * @param itemId Id of catalogue item to be retrieved
	 * @return The catalogue item specified by the id
	 * @throws CatalogueItemNotFoundException in case catalogue item is not available in memory
	 */
	public CatalogueItem getById(Integer itemId) throws CatalogueItemNotFoundException;
	
	/**
	 * Retrieves all comments for this catalogue item 
	 * @param item	The item specified
	 * @return A list of all the item's comments
	 */
	public List<Comment> getAllComments(CatalogueItem item);
	
	/**
	 * Retrieves a subset of the comments for the specified item specified by the start index and size
	 * @param item the specified item
	 * @param startIndex Start index for the sublist
	 * @param size Size of the sublist
	 * @return A subset of the item's comments
	 */
	public List<Comment> getSubsetOfComments(CatalogueItem item, Integer startIndex, Integer size);

	/**
	 * Adds a comment to this catalogue item
	 * @param item Item specified
	 * @param comment Comment to be added
	 * @param linkToComment Link for the new comment
	 */
	public void addComment(CatalogueItem item, Comment comment, Link linkToComment);

	/**
	 * Retrieves the number of remaning comments in the list given a start index
	 * @param currentCatalogueItem item specified
	 * @param start Start index for the sublist
	 * @return The number of remaining comments in the list
	 */
	public int getNumberOfRemainingComments(CatalogueItem currentCatalogueItem, Integer start);
	
	/**
	 * Adds a follower to this item
	 * @param item Item specified
	 * @param follower Follower to be added to the item
	 */
	public void addFollower(CatalogueItem item, User follower);
	
	/**
	 * Retrieves the list of followers fo this item
	 * @param item item specified
	 * @return List of followers for the item
	 */
	public List<User> getAllFollowers(CatalogueItem item);

}

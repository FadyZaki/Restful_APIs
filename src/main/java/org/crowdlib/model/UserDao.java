package org.crowdlib.model;

import java.util.List;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.Notification;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.UserNotFoundException;

public interface UserDao {

	/**
	 * Retrieves the user using a username
	 * @param username Username specified
	 * @return A user which has this username
	 * @throws UserNotFoundException in case user is not in memory
	 */
	public User getUserByUsername(String username) throws UserNotFoundException;
	
	/**
	 * Retrieve the comments that are the user's favourite comments
	 * @param user User specified
	 * @return List of user's favourite comments
	 */
	public List<Comment> getUserFavouriteComments(User user);
		
	/**
	 * Checks if the specified comment is among the user's favourite list
	 * @param user User specified
	 * @param comment comment specified
	 * @return True if comment is among favourites
	 */
	boolean checkIfCommentIsAmongFavourites(User user, Comment comment);

	/**
	 * Adds a comment to user's favourites list
	 * @param user user specified
	 * @param comment comment specified
	 */
	public void addCommentToFavourites(User user, Comment comment);
	
	/**
	 * Retrieves the a list of user's followed items
	 * @param user user specified
	 * @return list of user's followed items
	 */
	public List<CatalogueItem> getUserFollowedItems(User user);

	/**
	 * Checks if item is among the user's followed items
	 * @param user user specified
	 * @param item item specified
	 * @return true if item is among followed items
	 */
	public boolean checkIfItemIsAmongFollowedItems(User user, CatalogueItem item);

	/**
	 * Adds the specified item to the user's followed items
	 * @param user user specified 
	 * @param item item specified
	 */
	public void addItemToFollowedItems(User user, CatalogueItem item);
	
	/**
	 * Retrieves the list of notifications for this user
	 * @param user user specified
	 * @return List of notifications for the user
	 */
	public List<Notification> getListOfNotifications(User user);
	
	/**
	 * Adds a notification to the user's list of notifications
	 * @param user User specified
	 * @param notification notification specified
	 */
	public void addToListOfNotifications(User user, Notification notification);
	
	/**
	 * Removes notification from the user's list of notifications when the corresponding
	 * comment is seen
	 * @param user user specified
	 * @param comment notification's comment
	 */
	public void adjustListOfNotificationsAfterCommentIsSeen(User user, Comment comment);

	/**
	 * Removes notifications from the user's list of notifications when the corresponding
	 * comments are seen
	 * @param user user specified
	 * @param comments list of comments to be checked against the notifications list
	 */
	public void adjustListOfNotificationsAfterAListOfCommentsIsSeen(User currentUser, List<Comment> comments);
	
}

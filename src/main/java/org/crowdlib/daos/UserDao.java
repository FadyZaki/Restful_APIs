package org.crowdlib.daos;

import java.util.List;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.Notification;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.UserNotFoundException;

public interface UserDao {

	public User getUserByUsername(String username) throws UserNotFoundException;
	
	public List<Comment> getUserFavouriteComments(User user);
		
	boolean checkIfCommentIsAmongFavourites(User user, Comment comment);

	public void addCommentToFavourites(User user, Comment comment);
	
	public List<CatalogueItem> getUserFollowedItems(User user);

	public boolean checkIfItemIsAmongFavourites(User user, CatalogueItem item);

	public void addItemToFollowedItems(User user, CatalogueItem item);
	
	public List<Notification> getListOfNotifications(User user);
	
	public void addToListOfNotifications(User user, Notification notification);
	
	public void adjustListOfNotificationsAfterCommentIsSeen(User user, Comment comment);

	public void adjustListOfNotificationsAfterAListOfCommentsIsSeen(User currentUser, List<Comment> comments);
	
}

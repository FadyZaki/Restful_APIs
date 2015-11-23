package org.crowdlib.daos;

import java.util.Iterator;
import java.util.List;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.Notification;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.UserNotFoundException;
import org.crowdlib.inmemory.collections.InMemoryUserCollection;

public class UserDaoImpl implements UserDao {

	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		User currentUser = InMemoryUserCollection.getUser(username);
		if(currentUser == null) throw new UserNotFoundException("Not a registered user");
		return currentUser;
	}
	
	@Override
	public List<Comment> getUserFavouriteComments(User user) {
		return user.getFavouriteComments();
	}

	@Override
	public boolean checkIfCommentIsAmongFavourites(User user, Comment comment) {
		return user.getFavouriteComments().contains(comment);
		
	}
	
	@Override
	public void addCommentToFavourites(User user, Comment comment) {
		user.getFavouriteComments().add(comment);
	}

	@Override
	public List<CatalogueItem> getUserFollowedItems(User user) {
		return user.getFollowedItems();
	}
	
	@Override
	public boolean checkIfItemIsAmongFavourites(User user, CatalogueItem item) {
		return user.getFollowedItems().contains(item);	
	}
	
	@Override
	public void addItemToFollowedItems(User user, CatalogueItem item) {
		user.getFollowedItems().add(item);
	}

	@Override
	public void addToListOfNotifications(User user, Notification notification) {
		user.getNotifications().add(notification);	
	}

	@Override
	public List<Notification> getListOfNotifications(User user) {
		return user.getNotifications();
		
	}

	@Override
	public void adjustListOfNotificationsAfterCommentIsSeen(User user, Comment comment) {
		List<Notification> notifications = user.getNotifications();
		for(Iterator<Notification> itr = notifications.iterator();itr.hasNext();)
        {
            Notification notification = itr.next();
            if(notification.getComment().equals(comment))
				itr.remove();
        }
	}

	@Override
	public void adjustListOfNotificationsAfterAListOfCommentsIsSeen(User currentUser, List<Comment> comments) {
		List<Notification> notifications = currentUser.getNotifications();
		for(Iterator<Notification> itr = notifications.iterator();itr.hasNext();)
        {
            Notification notification = itr.next();
            if(comments.contains(notification.getComment()))
				itr.remove();
        }
	}
	
	

}

package org.crowdlib.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import javax.ws.rs.core.Link;

import org.mockito.Mock;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.Notification;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.exceptions.UserNotFoundException;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.crowdlib.inmemory.collections.InMemoryUserCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoImplTest {

	@Mock
	CatalogueItem mockItem;
	
	@Mock
	Comment mockComment;
	
	@Mock
	User mockUser;
	
	@Mock
	Link mockLink;
	
	UserDaoImpl userDaoImpl = new UserDaoImpl();

	@Mock
	Notification mockNotification;
	
	@Before
	public void setup() {
		InMemoryUserCollection.initializeInMemoryUsers();
		when(mockUser.getUsername()).thenReturn("mock_user");
		InMemoryUserCollection.addUser(mockUser);
	}
	
	@Test
	public void whenGetUserByUsernameIsCalledItShouldReturnTheUserAvailableInMemory() throws UserNotFoundException {
		//given
		User currentMockUser = mock(User.class);
		when(currentMockUser.getUsername()).thenReturn("current_mock_user");
		InMemoryUserCollection.addUser(currentMockUser);
		
		//when
		User user = this.userDaoImpl.getUserByUsername(currentMockUser.getUsername());
				
		//then
		assertNotNull(user);
		assertNotEquals(mockUser, user);
		assertEquals(currentMockUser, user);	
	}
	
	@Test(expected=UserNotFoundException.class)
	public void whenGetUserByUsernameIsCalledItShouldThrowUserNotFoundExceptionIfUserIsNotAvailableInMemory() throws UserNotFoundException{
		//given
		User currentMockUser = mock(User.class); //user not added to memory
		when(currentMockUser.getUsername()).thenReturn("new_mock_user");
		
		//when
		this.userDaoImpl.getUserByUsername(currentMockUser.getUsername());
				
		//then
		//UserNotFoundException should be thrown
	}

	@Test
	public void whenGetUserFavouriteCommentsIsCalledOnANewUserItShouldReturnAnEmptyList() {
		//given
		User currentMockUser = mock(User.class);
		List<Comment> favouriteComments = new ArrayList<Comment>();
		when(currentMockUser.getFavouriteComments()).thenReturn(favouriteComments);
		when(currentMockUser.getUsername()).thenReturn("new_mock_user");
		InMemoryUserCollection.addUser(currentMockUser);
		
		//when
		List<Comment> favouriteCommentsReturnedByMethod = currentMockUser.getFavouriteComments();
		
		//then
		assertTrue(favouriteCommentsReturnedByMethod.isEmpty());
		
	}

	@Test
	public void whenCheckIfCommentIsAmongFavouritesIsCalledOnACommentAlreadyAvailableInFavouritesListItShouldReturnFalse() {
		//given
		List<Comment> favouriteComments = new ArrayList<Comment>();
		when(mockUser.getFavouriteComments()).thenReturn(favouriteComments);
		Comment anotherMockComment = mock(Comment.class);
		when(anotherMockComment.getId()).thenReturn(2313);
		
		//when
		this.userDaoImpl.addCommentToFavourites(mockUser, mockComment);
		
		//then
		assertTrue(this.userDaoImpl.checkIfCommentIsAmongFavourites(mockUser, mockComment));
		assertFalse(this.userDaoImpl.checkIfCommentIsAmongFavourites(mockUser, anotherMockComment));
	}

	@Test
	public void whenAddCommentToFavouritesIsCalledTheCommentShouldBeAddedTofavouriteCommentsList() {
		//given
		List<Comment> favouriteComments = new ArrayList<Comment>();
		when(mockUser.getFavouriteComments()).thenReturn(favouriteComments);
		int numberOfFavouriteCommentsBeforeAddingNewComment = mockUser.getFavouriteComments().size();
		
		//when
		this.userDaoImpl.addCommentToFavourites(mockUser, mockComment);
		int numberOfFavouriteCommentsAfterAddingNewComment = mockUser.getFavouriteComments().size();
		
		//then
		assertEquals(numberOfFavouriteCommentsBeforeAddingNewComment+1, numberOfFavouriteCommentsAfterAddingNewComment);
		assertTrue(mockUser.getFavouriteComments().contains(mockComment));
	}
	
	@Test
	public void whenGetUserFollowedItemsIsCalledOnANewUserItShouldReturnAnEmptyList() {
		//given
		User currentMockUser = mock(User.class);
		List<CatalogueItem> followedItems = new ArrayList<CatalogueItem>();
		when(currentMockUser.getFollowedItems()).thenReturn(followedItems);
		when(currentMockUser.getUsername()).thenReturn("new_mock_user");
		InMemoryUserCollection.addUser(currentMockUser);
		
		//when
		List<CatalogueItem> followedItemsReturnedByMethod = currentMockUser.getFollowedItems();
		
		//then
		assertTrue(followedItemsReturnedByMethod.isEmpty());
		
	}
	
	@Test
	public void whenCheckIfItemIsAmongFollowedItemsIsIsCalledOnAnItemAlreadyAvailableInFollowedListItShouldReturnFalse() {
		//given
		List<CatalogueItem> followedItems = new ArrayList<CatalogueItem>();
		when(mockUser.getFollowedItems()).thenReturn(followedItems);
		CatalogueItem anotherMockItem = mock(CatalogueItem.class);
		when(anotherMockItem.getId()).thenReturn(2313);
		
		//when
		this.userDaoImpl.addItemToFollowedItems(mockUser, mockItem);
		
		//then
		assertTrue(this.userDaoImpl.checkIfItemIsAmongFollowedItems(mockUser, mockItem));
		assertFalse(this.userDaoImpl.checkIfItemIsAmongFollowedItems(mockUser, anotherMockItem));
	}

	@Test
	public void whenAddItemToFollowedItemsIsCalledTheItemShouldBeAddedToFollowedItemsList() {
		//given
		List<CatalogueItem> followedItems = new ArrayList<CatalogueItem>();
		when(mockUser.getFollowedItems()).thenReturn(followedItems);
		int numberOfFollowedItemsBeforeAddingNewItem = mockUser.getFollowedItems().size();
		
		//when
		this.userDaoImpl.addItemToFollowedItems(mockUser, mockItem);
		int numberOfFollowedItemsAfterAddingNewItem = mockUser.getFollowedItems().size();
		
		//then
		assertEquals(numberOfFollowedItemsBeforeAddingNewItem+1, numberOfFollowedItemsAfterAddingNewItem);
		assertTrue(mockUser.getFollowedItems().contains(mockItem));
	}

	@Test
	public void whenAddToListOfNotificationsIsCalledTheNotificationShouldBeAddedToNotificationList() {
		//given
		List<Notification> notificaitons = new ArrayList<Notification>();
		when(mockUser.getNotifications()).thenReturn(notificaitons);
		int numberOfNotificaitonsBeforeAddingNewNotificaiton = mockUser.getNotifications().size();
		
		//when
		this.userDaoImpl.addToListOfNotifications(mockUser, mockNotification);
		int numberOfNotificationsAfterAddingNewNotification = mockUser.getNotifications().size();
		
		//then
		assertEquals(numberOfNotificaitonsBeforeAddingNewNotificaiton+1,  numberOfNotificationsAfterAddingNewNotification);
		assertTrue(mockUser.getNotifications().contains(mockNotification));
	}

	@Test
	public void whenGetUserNotificationsIsCalledOnANewUserItShouldReturnAnEmptyList() {
		//given
		User currentMockUser = mock(User.class);
		List<Notification> Notifications = new ArrayList<Notification>();
		when(currentMockUser.getNotifications()).thenReturn(Notifications);
		when(currentMockUser.getUsername()).thenReturn("new_mock_user");
		InMemoryUserCollection.addUser(currentMockUser);
		
		//when
		List<Notification> notificationsReturnedByMethod = currentMockUser.getNotifications();
		
		//then
		assertTrue(notificationsReturnedByMethod.isEmpty());
		
	}

	@Test
	public void whenAdjustListOfNotificationsAfterCommentIsSeenIsCalledWithACertainCommentItShouldRemoveTheNotificationOfThisCommentFromListOfNotification() {
		//given
		List<Notification> notifications = new ArrayList<Notification>();
		when(mockUser.getNotifications()).thenReturn(notifications);
		when(mockNotification.getComment()).thenReturn(mockComment);
		notifications.add(mockNotification);
		int numberOfNotificationsBeforeCommentIsSeen = mockUser.getNotifications().size();
		
		//when
		this.userDaoImpl.adjustListOfNotificationsAfterCommentIsSeen(mockUser, mockComment);
		int numberOfNotificationsAfterCommentIsSeen = mockUser.getNotifications().size();
		
		//then
		assertEquals(numberOfNotificationsBeforeCommentIsSeen - 1,  numberOfNotificationsAfterCommentIsSeen);
		assertFalse(mockUser.getNotifications().contains(mockNotification));
	}
	
	@Test
	public void whenAdjustListOfNotificationsAfterCommentIsSeenIsCalledWithACertainCommentItShouldNotRemoveAnotherCommentNotificationFromListOfNotification() {
		//given
		List<Notification> notifications = new ArrayList<Notification>();
		when(mockUser.getNotifications()).thenReturn(notifications);
		when(mockNotification.getComment()).thenReturn(mockComment);
		notifications.add(mockNotification);
		
		Comment anotherMockComment = mock(Comment.class);
		Notification anotherMockNotification = mock(Notification.class);
		when(anotherMockNotification.getComment()).thenReturn(anotherMockComment);
		notifications.add(anotherMockNotification);

		//when
		this.userDaoImpl.adjustListOfNotificationsAfterCommentIsSeen(mockUser, mockComment);
		
		//then
		assertTrue(mockUser.getNotifications().contains(anotherMockNotification));
	}
	
	@Test
	public void whenAdjustListOfNotificationsAfterAListOfCommentsIsSeenIsCalledWithAListOfCommentsItShouldRemoveTheNotificationOfAllTheseCommentsFromListOfNotification() {
		//given
		List<Notification> notifications = new ArrayList<Notification>();
		when(mockUser.getNotifications()).thenReturn(notifications);
		List<Comment> newComments = new ArrayList<Comment>();
		
		when(mockNotification.getComment()).thenReturn(mockComment);
		notifications.add(mockNotification);
		newComments.add(mockComment);
		
		Comment anotherMockComment = mock(Comment.class);
		Notification anotherMockNotification = mock(Notification.class);
		when(anotherMockNotification.getComment()).thenReturn(anotherMockComment);
		notifications.add(anotherMockNotification);
		newComments.add(anotherMockComment);
		
		int numberOfNotificationsBeforeCommentIsSeen = mockUser.getNotifications().size();
		
		//when
		this.userDaoImpl.adjustListOfNotificationsAfterAListOfCommentsIsSeen(mockUser, newComments);
		int numberOfNotificationsAfterCommentIsSeen = mockUser.getNotifications().size();
		
		//then
		assertEquals(numberOfNotificationsBeforeCommentIsSeen - 2,  numberOfNotificationsAfterCommentIsSeen);
		assertFalse(mockUser.getNotifications().contains(mockNotification));
		assertFalse(mockUser.getNotifications().contains(anotherMockNotification));
	}
	
	@Test
	public void whenAdjustListOfNotificationsAfterAListOfCommentsIsSeenIsCalledWithAListOfCommentsItShouldNotRemoveAnotherCommentNotificationFromListOfNotification() {
		//given
		List<Notification> notifications = new ArrayList<Notification>();
		when(mockUser.getNotifications()).thenReturn(notifications);
		List<Comment> newComments = new ArrayList<Comment>();
		
		when(mockNotification.getComment()).thenReturn(mockComment);
		notifications.add(mockNotification);
		newComments.add(mockComment);
		
		Comment anotherMockComment = mock(Comment.class);
		Notification anotherMockNotification = mock(Notification.class);
		when(anotherMockNotification.getComment()).thenReturn(anotherMockComment);
		notifications.add(anotherMockNotification);
		//anotherMockComment is not added to the list of seen comments
		
		//when
		this.userDaoImpl.adjustListOfNotificationsAfterAListOfCommentsIsSeen(mockUser, newComments);
		
		//then
		assertTrue(mockUser.getNotifications().contains(anotherMockNotification));
	}

}

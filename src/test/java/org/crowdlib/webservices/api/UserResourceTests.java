package org.crowdlib.webservices.api;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.SecurityContext;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.Notification;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.crowdlib.inmemory.collections.InMemoryCommentCollection;
import org.crowdlib.inmemory.collections.InMemoryUserCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTests {

	@Mock
	private SecurityContext mockSecurityContext;
	@Mock
	private Principal mockPrincipal;
	@Mock
	private User mockUser;
	@Mock
	private Comment mockComment;
	@Mock
	private CatalogueItem mockItem;
	
	private UserResource userResource = new UserResource();
	
	@Before
	public void setup() {
		InMemoryUserCollection.initializeInMemoryUsers();
		InMemoryCatalogueItemCollection.initializeInMemoryCatalogueItems();
		InMemoryCommentCollection.initializeInMemoryComments();
		this.userResource.setSecurityContext(mockSecurityContext);
		when(mockSecurityContext.getUserPrincipal()).thenReturn(mockPrincipal);
		when(mockPrincipal.getName()).thenReturn("user");
		when(mockUser.getUsername()).thenReturn("user");
		when(mockComment.getId()).thenReturn(1121);
		when(mockItem.getId()).thenReturn(1121);
		InMemoryUserCollection.addUser(mockUser);
		InMemoryCommentCollection.addComment(mockComment);
		InMemoryCatalogueItemCollection.addCatalogueItem(mockItem);
	}
	
	@Test
	public void whenGetUserIsCalledItShouldAlwaysReturnTheCurrentLoggedInUserIfAvailableInMemory() {
		//given
		when(mockPrincipal.getName()).thenReturn("current_logged_in_user"); //current logged in user's username "current_logged_in_user"
		
		when(mockUser.getUsername()).thenReturn("current_logged_in_user");
		InMemoryUserCollection.addUser(mockUser);
		
		User anotherMockedUser = mock(User.class);
		when(anotherMockedUser.getUsername()).thenReturn("not_logged_in_user");
		InMemoryUserCollection.addUser(anotherMockedUser);

		//when
		User user = this.userResource.getCurrentLoggedUser();
		
		//then
		assertEquals(mockUser, user);
		assertNotEquals(anotherMockedUser, user);
	}
	
	@Test(expected=CustomizedWebApplicationException.class)
	public void whenGetUserIsCalledItShouldThrowCustomizedWebApplicationExceptionIfUserIsNotAvailableInMemory() {
		//given
		when(mockPrincipal.getName()).thenReturn("new_user");
		User anotherMockedUser = mock(User.class);
		when(anotherMockedUser.getUsername()).thenReturn("new_user");

		//when
		this.userResource.getCurrentLoggedUser();
		
		//then
		//Customized WebApplicationException is Thrown because user is not available in memory
	}

	@Test
	public void whenAUserIsANewUserHeShouldHaveNoFavouritesComments() {
		//given
		ArrayList<Comment> comments = new ArrayList<Comment>();
		when(mockUser.getFavouriteComments()).thenReturn(comments);
		
		//when
		List<Comment> favouriteCommentsReturnedFromMethod = this.userResource.getUserFavouriteComments();
		
		//then
		assertEquals(0, favouriteCommentsReturnedFromMethod.size());
		
	}

	@Test
	public void whenGetFavouriteCommentsIsCalledItShouldReturnAllCommentsAvailableInTheUserFavouriteList(){
		//given
		Comment anotherMockComment = mock(Comment.class);
		ArrayList<Comment> comments = new ArrayList<Comment>();
		comments.add(mockComment);
		comments.add(anotherMockComment);
		when(mockUser.getFavouriteComments()).thenReturn(comments);
		
		//when
		List<Comment> favouriteComments = this.userResource.getUserFavouriteComments();
		
		//then
		assertTrue(favouriteComments.contains(mockComment));
		assertTrue(favouriteComments.contains(anotherMockComment));
	}
	
	@Test
	public void whenACommentIsAddedToUserFavouritesItShouldChangeTheSizeOfListOfFavourites(){
		//given
		List<Comment> favouriteComments = new ArrayList<Comment>();
		when(mockUser.getFavouriteComments()).thenReturn(favouriteComments);
		int favouritesListSizeBeforeAddingComment = mockUser.getFavouriteComments().size();
		
		//when
		this.userResource.addCommentToUserFavourites(mockComment.getId());
		int favouritesListSizeAfterAddingComment = mockUser.getFavouriteComments().size(); 
		
		//then
		assertEquals(favouritesListSizeBeforeAddingComment + 1, favouritesListSizeAfterAddingComment);
	}
	
	@Test
	public void whenAddCommentToFavouritesIsCalledWithACommentThatAlreadyExistsInFavouritesListItShouldNotBeAddedToFavouritesListAgain(){
		//given
		List<Comment> favouriteComments = new ArrayList<Comment>();
		when(mockUser.getFavouriteComments()).thenReturn(favouriteComments);
		
		//when
		this.userResource.addCommentToUserFavourites(mockComment.getId());
		int favouritesListSizeBeforeAddingDuplicateComment = mockUser.getFavouriteComments().size();
		this.userResource.addCommentToUserFavourites(mockComment.getId());
		int favouritesListSizeAfterAddingDuplicateComment = mockUser.getFavouriteComments().size(); 
		
		//then
		assertEquals(favouritesListSizeBeforeAddingDuplicateComment, favouritesListSizeAfterAddingDuplicateComment);
	}
	
	@Test
	public void whenAddCommentToFavouritesIsCalledWithANewCommentThenTheCountOfFavouritesForThisCommentShouldBeIncremented(){
		//given
		Comment comment = new Comment();
		InMemoryCommentCollection.addComment(comment);
		int commentFavouritesCountBeforeAddingCommentToFavourties = comment.getFavouritesCount();
		
		//when
		this.userResource.addCommentToUserFavourites(comment.getId());
		int commentFavouritesCountAfterAddingCommentToFavourties = comment.getFavouritesCount();
		
		//then
		assertEquals(commentFavouritesCountBeforeAddingCommentToFavourties + 1, commentFavouritesCountAfterAddingCommentToFavourties);
	}
	
	@Test
	public void whenAUserIsANewUserHeShouldHaveNoFollowedItems() {
		//given
		ArrayList<CatalogueItem> items = new ArrayList<CatalogueItem>();
		when(mockUser.getFollowedItems()).thenReturn(items);
		
		//when
		List<CatalogueItem> followedItemsReturnedFromMethod = this.userResource.getUserFollowedItems();
		
		//then
		assertEquals(0, followedItemsReturnedFromMethod.size());
		
	}
	
	@Test
	public void whenGetFollowedItemsIsCalledItShouldReturnAllItemsAvailableInTheUserFollowedItemsList(){
		//given
		CatalogueItem anotherMockItem = mock(CatalogueItem.class);
		ArrayList<CatalogueItem> items = new ArrayList<CatalogueItem>();
		items.add(mockItem);
		items.add(anotherMockItem);
		when(mockUser.getFollowedItems()).thenReturn(items);
		
		//when
		List<CatalogueItem> followedItemsReturnedFromMethod = this.userResource.getUserFollowedItems();
		
		//then
		assertTrue(followedItemsReturnedFromMethod.contains(mockItem));
		assertTrue(followedItemsReturnedFromMethod.contains(anotherMockItem));
	}
	
	@Test
	public void whenAnItemIsAddedToUserFavouritesItShouldChangeTheSizeOfListOfFollowedItems(){
		//given
		ArrayList<CatalogueItem> items = new ArrayList<CatalogueItem>();
		when(mockUser.getFollowedItems()).thenReturn(items);
		int followedItemsListSizeBeforeAddingComment = mockUser.getFollowedItems().size();
		
		//when
		this.userResource.addItemToUserFollowedItems(mockItem.getId());
		int followedItemsListSizeAfterAddingComment  = mockUser.getFollowedItems().size(); 
		
		//then
		assertEquals(followedItemsListSizeBeforeAddingComment + 1, followedItemsListSizeAfterAddingComment);
	}
	
	@Test
	public void whenAddItemToFavouritesIsCalledWithAnItemThatAlreadyExistsInFollowedItemsListItShouldNotBeAddedAgain(){
		//given
		ArrayList<CatalogueItem> items = new ArrayList<CatalogueItem>();
		when(mockUser.getFollowedItems()).thenReturn(items);
		
		//when
		this.userResource.addItemToUserFollowedItems(mockItem.getId());
		int followedItemsListSizeBeforeAddingDuplicateItem = mockUser.getFollowedItems().size();
		this.userResource.addCommentToUserFavourites(mockComment.getId());
		int followedItemsListSizeAfterAddingDuplicateItem = mockUser.getFollowedItems().size();
		
		//then
		assertEquals(followedItemsListSizeBeforeAddingDuplicateItem, followedItemsListSizeAfterAddingDuplicateItem);
	}
	
	@Test
	public void whenAddItemToFollowedItemsIsCalledWithANewItemThenTheCountOfFollowersForThisItemShouldBeIncremented(){
		//given
		ArrayList<User> followers = new ArrayList<User>();
		when(mockItem.getFollowers()).thenReturn(followers);
		int followersListSizeBeforeAddingItemToFollowedItems = mockItem.getFollowers().size();
		
		//when
		this.userResource.addItemToUserFollowedItems(mockItem.getId());
		int followersListSizeAfterAddingItemToFollowedItems = mockItem.getFollowers().size();
		
		//then
		assertEquals(followersListSizeBeforeAddingItemToFollowedItems + 1, followersListSizeAfterAddingItemToFollowedItems);
	}

	@Test
	public void whenAUserIsANewUserHeShouldHaveNoNotifications() {
		//given
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		when(mockUser.getNotifications()).thenReturn(notifications);
		
		//when
		List<Notification> notificationsListReturnedFromMethod = this.userResource.getUserNotifications();
		
		//then
		assertEquals(0, notificationsListReturnedFromMethod.size());
		
	}

	@Test
	public void whenGetUserNotificationsIsCalledItShouldReturnAllNotificationsAvailableFortheUser(){
		//given
		Notification mockNotification = mock(Notification.class);
		Notification anotherMockNotification = mock(Notification.class);
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		notifications.add(mockNotification);
		notifications.add(anotherMockNotification);
		when(mockUser.getNotifications()).thenReturn(notifications);
		
		//when
		List<Notification> notificationsReturnedFromMethod = this.userResource.getUserNotifications();
		
		//then
		assertTrue(notificationsReturnedFromMethod.contains(mockNotification));
		assertTrue(notificationsReturnedFromMethod.contains(anotherMockNotification));
	}

}

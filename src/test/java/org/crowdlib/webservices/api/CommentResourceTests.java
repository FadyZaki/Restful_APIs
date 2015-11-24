package org.crowdlib.webservices.api;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.crowdlib.constants.ExplanatoryMessagesConstants;
import org.crowdlib.constants.RoleTypeConstants;
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
public class CommentResourceTests {

	@Mock
	private Comment mockComment;
	@Mock
	private SecurityContext mockSecurityContext;
	@Mock
	private Principal mockPrincipal;
	@Mock
	private UriInfo mockUriInfo;
	@Mock
	private UriBuilder mockUriBuilder;
	@Mock
	private User mockUser;
	@Mock
	private CatalogueItem mockItem;

	public static final String APPLICATION_PATH = "http://localhost:9998/";
	public static final URI BASE_URI = URI.create(APPLICATION_PATH);

	CommentResource commentResource = new CommentResource();

	@Before
	public void setup() {
		InMemoryUserCollection.initializeInMemoryUsers();
		InMemoryCatalogueItemCollection.initializeInMemoryCatalogueItems();
		InMemoryCommentCollection.initializeInMemoryComments();
		when(mockItem.getId()).thenReturn(11232);
		InMemoryCatalogueItemCollection.addCatalogueItem(mockItem);
		when(mockSecurityContext.getUserPrincipal()).thenReturn(mockPrincipal);
		when(mockPrincipal.getName()).thenReturn("student1");
		this.commentResource.setSecurityContext(mockSecurityContext);
		when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
		when(mockUriBuilder.path(anyString())).thenReturn(mockUriBuilder);
		when(mockUriBuilder.build()).thenReturn(BASE_URI);
		this.commentResource.setUriInfo(mockUriInfo);
	}

	@Test
	public void whenGetCommentIsCalledOnCommentAvailableInMemoryItShouldReturnThatComment() {
		// given:
		when(mockComment.getId()).thenReturn(111);
		// when:
		InMemoryCommentCollection.addComment(mockComment);
		Comment commentReturnedByMethod = this.commentResource.getComment(mockComment.getId());
		// then:
		verify(mockSecurityContext, times(1)).getUserPrincipal();
		verify(mockPrincipal, times(1)).getName();
		assertEquals(mockComment, commentReturnedByMethod);
	}

	@Test(expected = CustomizedWebApplicationException.class)
	public void whenGetCommentIsCalledOnCommentNotAvailableInMemoryItShouldThrowCustomizedWebApplicationExceptionWithResponseNotFound() {
		// given:
		when(mockComment.getId()).thenReturn(123213);
		// when
		this.commentResource.getComment(mockComment.getId());
		// then
		// CustomizedWebApplication exception is thrown because comment isn't
		// available in memory
	}

	@Test
	public void whenCommentisAddedToCatalogueItemItShouldBeAvailableInMemory() {
		// given
		int itemId = 1; // item available in memory
		String commentContent = "Testing";
		// when
		Comment commentReturnedByMethod = this.commentResource.addCommentToCatalogueItem(itemId, commentContent);
		// then
		assertEquals(InMemoryCommentCollection.getComment(commentReturnedByMethod.getId()), commentReturnedByMethod);
	}

	@Test
	public void whenCommentisAddedToCatalogueItemItShouldBeAvailableInTheListOfTheCatalogueItemComments() {
		// given
		int itemId = 1; // item available in memory
		String commentContent = "Testing";
		// when
		Comment commentReturnedByMethod = this.commentResource.addCommentToCatalogueItem(itemId, commentContent);
		// then
		assertTrue(InMemoryCatalogueItemCollection.getCatalogueItem(itemId).getComments()
				.contains(commentReturnedByMethod));
	}

	@Test
	public void whenCommentisAddedToCatalogueItemItShouldAdjustItsLinkToItself() {
		// given
		int itemId = 1; // item available in memory
		String commentContent = "Testing";
		String pathToComments = APPLICATION_PATH + "items/comments";
		String pathToThisComment = pathToComments + "/" + String.valueOf(Comment.numberOfComments + 1);
		when(mockUriBuilder.build()).thenReturn(URI.create(pathToThisComment));
		// when
		Comment commentReturnedByMethod = this.commentResource.addCommentToCatalogueItem(itemId, commentContent);
		Link actualLinkToCommentReturnedByMethod = commentReturnedByMethod.getLinkToSelf();
		Link expectedLinkToCommentReturnedByMethod = Link.fromPath(pathToThisComment).rel("self").build();
		// then
		assertEquals(expectedLinkToCommentReturnedByMethod, actualLinkToCommentReturnedByMethod);
	}
	
	@Test
	public void whenCommentisAddedToCatalogueItemItShouldAdjustLinkToItsReplies() {
		// given
		int itemId = 1; // item available in memory
		String commentContent = "Testing Comment";
		String pathToComments = APPLICATION_PATH + "items/comments";
		String pathToThisComment = pathToComments + "/" + String.valueOf(Comment.numberOfComments + 1);
		when(mockUriBuilder.build()).thenReturn(URI.create(pathToThisComment));
		String pathToThisCommentReplies = pathToThisComment + "/replies";
		// when
		Comment commentReturnedByMethod = this.commentResource.addCommentToCatalogueItem(itemId, commentContent);
		Link actualLinkToRepliesReturnedByMethod = commentReturnedByMethod.getLinkToReplies();
		Link expectedLinkToRepliesReturnedByMethod = Link.fromPath(pathToThisCommentReplies).rel("replies").build();
		// then
		assertEquals(expectedLinkToRepliesReturnedByMethod, actualLinkToRepliesReturnedByMethod);
	}
	
	@Test
	public void whenCommentisAddedToCatalogueItemItShouldBeAddedToTheListOfNotificationsOfTheItemFollowers() {
		// given
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		when(mockUser.getNotifications()).thenReturn(notifications);
		ArrayList<User> followers = new ArrayList<User>();
		followers.add(mockUser);
		when(mockItem.getFollowers()).thenReturn(followers);
		
		// when
		int numberOfNotificationsBeforeCommentIsAdded = mockUser.getNotifications().size();
		this.commentResource.addCommentToCatalogueItem(mockItem.getId(), "new comment");
		int numberOfNotificationsAfterCommentIsAdded = mockUser.getNotifications().size();

		// then
		assertEquals(numberOfNotificationsBeforeCommentIsAdded + 1, numberOfNotificationsAfterCommentIsAdded);
	}

	@Test
	public void whenReplyIsAddedToACommentItShouldBeAvailableInTheListOfRepliesOfThisComment() {
		// given
		Comment commentUnderTest = InMemoryCommentCollection.getComment(1);
		String replyContent = "Testing Reply";
		String pathToComments = APPLICATION_PATH + "items/comments";
		String pathToThisComment = pathToComments + "/" + String.valueOf(commentUnderTest.getId());
		when(mockUriInfo.getAbsolutePath()).thenReturn(URI.create(pathToThisComment));
		// when
		Comment replyReturnedByMethod = this.commentResource.addReplyToComment(commentUnderTest.getId(), replyContent);
		// then
		assertTrue(InMemoryCommentCollection.getComment(commentUnderTest.getId()).getReplies().contains(replyReturnedByMethod));
	}
	
	@Test
	public void whenReplyisAddedToCommentItShouldAdjustItsLinkToItself() {
		// given
		Comment commentUnderTest = InMemoryCommentCollection.getComment(1);
		String replyContent = "Testing Reply";
		String pathToComments = APPLICATION_PATH + "items/comments";
		String pathToThisComment = pathToComments + "/" + String.valueOf(commentUnderTest.getId());
		when(mockUriInfo.getAbsolutePath()).thenReturn(URI.create(pathToThisComment));
		// when
		Comment replyReturnedByMethod = this.commentResource.addReplyToComment(commentUnderTest.getId(), replyContent);
		Link actualLinkToReplyReturnedByMethod = replyReturnedByMethod.getLinkToSelf();
		Link expectedLinkToReplyReturnedByMethod = Link.fromPath(pathToComments + "/" + String.valueOf(replyReturnedByMethod.getId())).rel("self").build();
		// then
		assertEquals(expectedLinkToReplyReturnedByMethod, actualLinkToReplyReturnedByMethod);
	}
	
	@Test
	public void whenReplyisAddedToCommentItShouldAdjustItsLinkToItsReplies() {
		// given
		Comment commentUnderTest = InMemoryCommentCollection.getComment(1);
		String replyContent = "Testing Reply";
		String pathToComments = APPLICATION_PATH + "items/comments";
		String pathToThisReply = pathToComments + "/" + String.valueOf(Comment.numberOfComments + 1);
		String pathToThisReplyReplies = pathToThisReply + "/replies";
		when(mockUriInfo.getAbsolutePath()).thenReturn(URI.create(pathToThisReply));
		// when
		Comment replyReturnedByMethod = this.commentResource.addReplyToComment(commentUnderTest.getId(), replyContent);
		Link actualLinkToRepliesReturnedByMethod = replyReturnedByMethod.getLinkToReplies();
		Link expectedLinkToRepliesReturnedByMethod = Link.fromPath(pathToThisReplyReplies).rel("replies").build();
		// then
		assertEquals(expectedLinkToRepliesReturnedByMethod, actualLinkToRepliesReturnedByMethod);
	}

	@Test
	public void whenACommentIsDeleteByItsOwnerTheCommentContentShouldBeChangedToTheAppropriateExplanatoryMessage() {
		// given
		when(mockUser.getUsername()).thenReturn("owner");
		when(mockPrincipal.getName()).thenReturn("owner");
		InMemoryUserCollection.addUser(mockUser);
		Comment commentUnderTest = new Comment("newComment", mockUser, mockItem);
		InMemoryCommentCollection.addComment(commentUnderTest);
		
		//when
		this.commentResource.deleteComment(commentUnderTest.getId());
		
		//then
		verify(mockSecurityContext, times(1)).getUserPrincipal();
		verify(mockPrincipal, times(1)).getName();
		assertEquals(ExplanatoryMessagesConstants.OWNER_DELETION_MESSAGE, commentUnderTest.getCommentContent());
		
	}
	
	@Test
	public void whenACommentIsDeleteByAnAdminTheCommentContentShouldBeChangedToTheAppropriateExplanatoryMessage() {
		// given
		User mockAdmin = mock(User.class);
		when(mockAdmin.getUsername()).thenReturn("admin");
		when(mockPrincipal.getName()).thenReturn("admin");
		when(mockSecurityContext.isUserInRole(RoleTypeConstants.ADMIN_USER)).thenReturn(true);
		InMemoryUserCollection.addUser(mockAdmin);
		InMemoryUserCollection.addUser(mockUser);
		Comment commentUnderTest = new Comment("newComment", mockUser, mockItem);
		InMemoryCommentCollection.addComment(commentUnderTest);
		
		//when
		this.commentResource.deleteComment(commentUnderTest.getId());
		
		//then
		verify(mockSecurityContext, times(1)).getUserPrincipal();
		verify(mockPrincipal, times(1)).getName();
		verify(mockSecurityContext, times(1)).isUserInRole(RoleTypeConstants.ADMIN_USER);
		assertEquals(ExplanatoryMessagesConstants.ADMIN_DELETION_MESSAGE, commentUnderTest.getCommentContent());
		
	}
	
	@Test
	public void whenACommentIsDeletedBySomeoneOtherThanTheOwnerOrTheAdminTheCommentContentShouldNotBeChanged() {
		// given
		User mockSecondUser = mock(User.class);
		when(mockSecondUser.getUsername()).thenReturn("secondUser");
		when(mockPrincipal.getName()).thenReturn("secondUser");
		when(mockSecurityContext.isUserInRole(RoleTypeConstants.ADMIN_USER)).thenReturn(false);
		InMemoryUserCollection.addUser(mockSecondUser);
		InMemoryUserCollection.addUser(mockUser);
		String commentContentBeforeDeletion = "newComment";
		Comment commentUnderTest = new Comment(commentContentBeforeDeletion, mockUser, mockItem);
		InMemoryCommentCollection.addComment(commentUnderTest);
		
		//when
		this.commentResource.deleteComment(commentUnderTest.getId());
		
		//then
		verify(mockSecurityContext, times(1)).getUserPrincipal();
		verify(mockSecurityContext, times(1)).isUserInRole(RoleTypeConstants.ADMIN_USER);
		verify(mockPrincipal, times(1)).getName();
		assertEquals(commentContentBeforeDeletion, commentUnderTest.getCommentContent());
		
	}

}

package org.crowdlib.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.mockito.Mock;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CommentNotFoundException;
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.crowdlib.inmemory.collections.InMemoryCommentCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommentDaoImplTest {

	@Mock
	CatalogueItem mockItem;

	@Mock
	Comment mockComment;

	@Mock
	User mockUser;

	@Mock
	Link mockLink;

	CommentDaoImpl commentDao = new CommentDaoImpl();
	
	@Before
	public void setup() {
		when(mockComment.getId()).thenReturn(1121);
		InMemoryCommentCollection.addComment(mockComment);
	}

	@Test
	public void whenGetCommentByIdIsCalledItShouldReturnTheCommentAvailableInMemory() throws CommentNotFoundException {
		//given
		Comment currentMockComment = mock(Comment.class);
		when(currentMockComment.getId()).thenReturn(1234);
		InMemoryCommentCollection.addComment(currentMockComment);
		
		//when
		Comment comment = this.commentDao.getById(currentMockComment.getId());
				
		//then
		assertNotNull(comment);
		assertEquals(currentMockComment, comment);	
	}
	
	@Test(expected=CommentNotFoundException.class)
	public void whenGetCommentByIdIsCalledItShouldThrowCommentNotFoundExceptionIfCommentIsNotAvailableInMemory() throws CommentNotFoundException{
		//given
		Comment currentMockComment = mock(Comment.class); //Comment not added to memory
		when(currentMockComment.getId()).thenReturn(4321);
		
		//when
		this.commentDao.getById(currentMockComment.getId());
				
		//then
		//CommentNotFoundException should be thrown
	}

	@Test
	public void whenCreateCommentIsCalledItShouldAddThisCommentToMemory() {
		//when
		Comment comment = this.commentDao.createComment("newComment", mockUser, mockItem);
		
		//then
		assertNotNull(InMemoryCommentCollection.getComment(comment.getId()));
		assertEquals(comment,InMemoryCommentCollection.getComment(comment.getId()));
		
	}
	
	@Test
	public void whenCreateCommentIsCalledItShouldAddACommentHavingTheSameValuesSpecified() {
		//when
		Comment comment = this.commentDao.createComment("newComment", mockUser, mockItem);
		
		//then
		Comment commentInMemory = InMemoryCommentCollection.getComment(comment.getId());
		assertEquals("newComment",commentInMemory.getCommentContent());
		assertEquals(mockUser,commentInMemory.getOwner());
		assertEquals(mockItem,commentInMemory.getCatalogueItem());
		
	}

	@Test
	public void whenAddReplyIsCalledAReplyShouldBeAddedToTheSpecifiedComment() {
		//given
		Comment mockReply = mock(Comment.class);
		when(mockReply.getId()).thenReturn(21321);
		List<Comment> replies = new ArrayList<Comment>();
		when(mockComment.getReplies()).thenReturn(replies);
		
		//when
		this.commentDao.addReply(mockComment, mockReply);
		
		//then
		assertTrue(mockComment.getReplies().contains(mockReply));
	}
	
	@Test
	public void whenAddReplyIsCalledThenTheParentCommentToThisReplyShouldBeSetToTheSpecifiedComment() {
		//given
		Comment mockReply = new Comment();
		List<Comment> replies = new ArrayList<Comment>();
		when(mockComment.getReplies()).thenReturn(replies);
		
		//when
		this.commentDao.addReply(mockComment, mockReply);
		
		//then
		assertTrue(mockReply.getParentComment().equals(mockComment));
	}
	
	@Test
	public void whenAddReplyIsCalledThisReplyShouldBeAddedToMemory() {
		//given
		Comment mockReply = mock(Comment.class);
		
		//when
		this.commentDao.addReply(mockComment, mockReply);
		
		//then
		assertEquals(mockReply, InMemoryCommentCollection.getComment(mockReply.getId()));
	}

	@Test
	public void whenAdjustLinkToSelfIsCalledTheLinkSpecifiedShouldBeAddedToComment() {
		//given
		Comment comment = new Comment();
		
		//when
		this.commentDao.adjustLinkToSelf(comment, mockLink);
		
		//then
		assertNotNull(comment.getLinkToSelf());
		assertEquals(mockLink, comment.getLinkToSelf());
	}
	
	@Test
	public void whenAdjustLinkToRepliesIsCalledTheLinkSpecifiedShouldBeAddedToComment() {
		//given
		Comment comment = new Comment();
		
		//when
		this.commentDao.adjustLinkToReplies(comment, mockLink);
		
		//then
		assertNotNull(comment.getLinkToReplies());
		assertEquals(mockLink, comment.getLinkToReplies());
	}

	@Test
	public void whenIncrementFavouritesCountIsCalledTheFavouritesCountShouldBeIncreasedByOne() {
		//given
		Comment comment = new Comment();
		int favouritesCountBeforeIncrementing = comment.getFavouritesCount();
		
		//when
		this.commentDao.incrementFavouritesCount(comment);
		int favouritesCountAfterIncrementing = comment.getFavouritesCount();
		
		//then
		assertEquals(favouritesCountBeforeIncrementing + 1, favouritesCountAfterIncrementing);
	}

	@Test
	public void whenGetRepliesIsCalledOnANewCommentItShouldBeEmpty() {
		//given
		Comment comment = new Comment();
		
		//when
		List<Comment> newCommentReplies = this.commentDao.getReplies(comment);
		
		//then
		assertTrue(newCommentReplies.isEmpty());
	}
	
	@Test
	public void whenGetRepliesIsCalledAfterAddingAReplyItShouldReturnAListContainingThisReply() {
		//given
		Comment mockReply = mock(Comment.class);
		List<Comment> replies = new ArrayList<Comment>();
		when(mockComment.getReplies()).thenReturn(replies);
		
		//when
		commentDao.addReply(mockComment, mockReply);
		List<Comment> newCommentReplies = this.commentDao.getReplies(mockComment);
		
		//then
		assertTrue(newCommentReplies.contains(mockReply));
	}

	@Test
	public void whenIsCommentOwnerIsCalledWithAUserThatIsTheOwnerOfThisCommentItShouldReturnTrue() {
		//given
		Comment comment = new Comment();
		
		//when
		comment.setOwner(mockUser);
		
		//then
		assertNotNull(comment.getOwner());
		assertTrue(comment.getOwner().equals(mockUser));
	}
	
	@Test
	public void whenIsCommentOwnerIsCalledWithAUserThatIsNotTheOwnerOfThisCommentItShouldReturnFalse() {
		//given
		Comment comment = new Comment();
		
		User anotherMockUser = mock(User.class);
		
		//when
		comment.setOwner(anotherMockUser);
		
		//then
		assertFalse(comment.getOwner().equals(mockUser));
	}

	@Test
	public void whenDeleteCommentIsCalledOnACommentItsContentShouldBeEqualToTheDeletionMessage() {
		//given
		Comment comment = new Comment("new Comment", mockUser, mockItem);
		
		//when
		this.commentDao.deleteComment(comment, "Deletion Message");
		
		//then
		assertEquals("Deletion Message", comment.getCommentContent());
	}
	
	@Test
	public void whenDeleteCommentIsCalledOnACommentItsListOfRepliesShouldBeEmpty() {
		//given
		Comment comment = new Comment("new Comment", mockUser, mockItem);
		
		//when
		this.commentDao.deleteComment(comment, "Deletion Message");
		
		//then
		assertTrue(comment.getReplies().isEmpty());
	}
	
	@Test
	public void whenDeleteCommentIsCalledOnACommentItsLinkToRepliesShouldBeSetToNull() {
		//given
		Comment comment = new Comment("new Comment", mockUser, mockItem);
		
		//when
		this.commentDao.deleteComment(comment, "Deletion Message");
		
		//then
		assertNull(comment.getLinkToReplies());
	}

}

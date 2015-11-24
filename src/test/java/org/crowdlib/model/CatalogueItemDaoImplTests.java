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
import org.crowdlib.exceptions.CustomizedWebApplicationException;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CatalogueItemDaoImplTests {

	@Mock
	CatalogueItem mockItem;
	
	@Mock
	Comment mockComment;
	
	@Mock
	User mockUser;
	
	@Mock
	Link mockLink;
	
	CatalogueItemDaoImpl itemDaoImpl = new CatalogueItemDaoImpl();
	
	@Before
	public void setup() {
		when(mockItem.getId()).thenReturn(1121);
		InMemoryCatalogueItemCollection.addCatalogueItem(mockItem);
	}
	
	@Test
	public void whenGetItemByIdIsCalledItShouldReturnTheItemAvailableInMemory() throws CatalogueItemNotFoundException {
		//given
		CatalogueItem currentMockItem = mock(CatalogueItem.class);
		when(currentMockItem.getId()).thenReturn(1234);
		InMemoryCatalogueItemCollection.addCatalogueItem(currentMockItem);
		
		//when
		CatalogueItem item = this.itemDaoImpl.getById(currentMockItem.getId());
				
		//then
		assertNotNull(item);
		assertEquals(item, currentMockItem);	
	}
	
	@Test(expected=CatalogueItemNotFoundException.class)
	public void whenGetItemByIdIsCalledItShouldThrowCatalogueItemNotFoundExceptionIfItemIsNotAvailableInMemory() throws CatalogueItemNotFoundException{
		//given
		CatalogueItem currentMockItem = mock(CatalogueItem.class); //item not added to memory
		when(currentMockItem.getId()).thenReturn(4321);
		
		//when
		this.itemDaoImpl.getById(currentMockItem.getId());
				
		//then
		//CatalogueItemNotFoundException should be thrown
	}
	
	@Test
	public void whenGetAllCommentsIsCalledOnANewCatalogueItemItShouldReturnAnEmptyListOfComments() throws CatalogueItemNotFoundException{
		//given
		when(mockItem.getComments()).thenReturn(new ArrayList<Comment>());
		
		//when
		List<Comment> itemComments = this.itemDaoImpl.getAllComments(mockItem);
				
		//then
		assertTrue(itemComments.isEmpty());
	}
	
	@Test
	public void whenGetAllCommentsIsCalledOnACatalogueItemItShouldReturnAllCommentsForThatItem() {
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		when(mockItem.getComments()).thenReturn(itemComments);
		Comment mockComment = mock(Comment.class);
		Comment anotherMockComment = mock(Comment.class);
		itemComments.add(mockComment);
		itemComments.add(anotherMockComment);
		
		//when
		List<Comment> itemCommentsReturnedFromMethod = this.itemDaoImpl.getAllComments(mockItem);
				
		//then
		assertTrue(itemCommentsReturnedFromMethod.contains(mockComment));
		assertTrue(itemCommentsReturnedFromMethod.contains(anotherMockComment));
	}
	
	@Test
	public void whenGetSubsetOfCommentsIsCalledOnACatalogueItemWithStartIndexGreaterThanTheCommentsListSizeItShouldReturnAnEmptyList() {
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		when(mockItem.getComments()).thenReturn(itemComments);
		Comment mockComment = mock(Comment.class);
		Comment anotherMockComment = mock(Comment.class);
		itemComments.add(mockComment);
		itemComments.add(anotherMockComment);
		int startIndex = 4; // start index > 2
		
		
		//when
		List<Comment> itemCommentsReturnedFromMethod = this.itemDaoImpl.getSubsetOfComments(mockItem, startIndex, 1);
				
		//then
		assertTrue(itemCommentsReturnedFromMethod.isEmpty());
	}
	
	@Test
	public void whenGetSubsetOfCommentsIsCalledOnACatalogueItemWithStartIndexPlusSizeGreaterThanTheCommentsListSizeItShouldReturnASublistStartingFromStartIndexUntilListSize() {
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		when(mockItem.getComments()).thenReturn(itemComments);
		Comment firstMockComment = mock(Comment.class);
		Comment secondMockComment = mock(Comment.class);
		Comment thirdMockComment = mock(Comment.class);
		itemComments.add(firstMockComment);
		itemComments.add(secondMockComment);
		itemComments.add(thirdMockComment);
		int startIndex = 1; // start index < 3
		int size = 10; // start index + size > 3
		
		
		//when
		List<Comment> itemCommentsReturnedFromMethod = this.itemDaoImpl.getSubsetOfComments(mockItem, startIndex, size);
				
		//then
		assertTrue(itemCommentsReturnedFromMethod.contains(secondMockComment));
		assertTrue(itemCommentsReturnedFromMethod.contains(thirdMockComment));
		assertFalse(itemCommentsReturnedFromMethod.contains(firstMockComment));
		assertEquals(itemCommentsReturnedFromMethod.size(), 2);
	}
	
	@Test
	public void whenGetSubsetOfCommentsIsCalledOnACatalogueItemWithStartIndexPlusSizeLessThanTheCommentsListSizeItShouldReturnASublistAccordingToSpecifiedParameters() {
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		when(mockItem.getComments()).thenReturn(itemComments);
		Comment firstMockComment = mock(Comment.class);
		Comment secondMockComment = mock(Comment.class);
		Comment thirdMockComment = mock(Comment.class);
		Comment fourthMockComment = mock(Comment.class);
		itemComments.add(firstMockComment);
		itemComments.add(secondMockComment);
		itemComments.add(thirdMockComment);
		itemComments.add(fourthMockComment);
		int startIndex = 1; // start index < 4
		int size = 2; // start index + size < 4 
		
		
		//when
		List<Comment> itemCommentsReturnedFromMethod = this.itemDaoImpl.getSubsetOfComments(mockItem, startIndex, size);
				
		//then
		assertTrue(itemCommentsReturnedFromMethod.contains(secondMockComment));
		assertTrue(itemCommentsReturnedFromMethod.contains(thirdMockComment));
		assertFalse(itemCommentsReturnedFromMethod.contains(firstMockComment));
		assertFalse(itemCommentsReturnedFromMethod.contains(fourthMockComment));
		assertEquals(itemCommentsReturnedFromMethod.size(), 2);
	}
	
	@Test
	public void whenGetNumberOfRemainingCommentsIsCalledWithStartIndexGreaterThanListSizeItShouldReturnZero() {
		
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		when(mockItem.getComments()).thenReturn(itemComments);
		Comment firstMockComment = mock(Comment.class);
		Comment secondMockComment = mock(Comment.class);
		itemComments.add(firstMockComment);
		itemComments.add(secondMockComment);
		int startIndex = 10; // start index > 2
		
		
		//when
		Integer numberOfItemCommentsReturnedFromMethod = this.itemDaoImpl.getNumberOfRemainingComments(mockItem, startIndex);
				
		//then
		assertEquals(numberOfItemCommentsReturnedFromMethod, new Integer(0));
	}
	
	@Test
	public void whenGetNumberOfRemainingCommentsIsCalledWithStartIndexLessThanListSizeItShouldReturnRemainingNumberOfCommentsStartingFromThisIndex() {
		
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		when(mockItem.getComments()).thenReturn(itemComments);
		Comment firstMockComment = mock(Comment.class);
		Comment secondMockComment = mock(Comment.class);
		Comment thirdMockComment = mock(Comment.class);
		Comment fourthMockComment = mock(Comment.class);
		itemComments.add(firstMockComment);
		itemComments.add(secondMockComment);
		itemComments.add(thirdMockComment);
		itemComments.add(fourthMockComment);
		int startIndex = 2; // start index < 4
		
		
		//when
		Integer numberOfItemCommentsReturnedFromMethod = this.itemDaoImpl.getNumberOfRemainingComments(mockItem, startIndex);
				
		//then
		assertEquals(numberOfItemCommentsReturnedFromMethod, new Integer(2));
	}
	
	@Test
	public void whenAddCommentIsCalledOnAnItemTheCommentShouldBeAddedToThisCatalogueItem(){
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		when(mockItem.getComments()).thenReturn(itemComments);
		
		//when
		this.itemDaoImpl.addComment(mockItem, mockComment, mockLink);
		
		//then
		assertTrue(mockItem.getComments().contains(mockComment));
	}
	
	@Test
	public void whenAddCommentIsCalledOnAnItemALinkToThisCommentShouldBeAddedToThisCatalogueItem(){
		//given
		List<Comment> itemComments = new ArrayList<Comment>();
		List<Link> itemCommentsLinks = new ArrayList<Link>();
		when(mockItem.getComments()).thenReturn(itemComments);
		when(mockItem.getLinksToEachComment()).thenReturn(itemCommentsLinks);
		
		//when
		this.itemDaoImpl.addComment(mockItem, mockComment, mockLink);
		
		//then
		assertTrue(mockItem.getLinksToEachComment().contains(mockLink));
	}
	
	
	@Test
	public void whenGetAllCatalogueItemsIsCalledItShouldReturnAllCatalogueItemsAvailableInMemory(){
		//given
		CatalogueItem newMockItem = mock(CatalogueItem.class);
		when(newMockItem.getId()).thenReturn(1122);
		InMemoryCatalogueItemCollection.addCatalogueItem(newMockItem);
		
		//when
		List<CatalogueItem> allCatalogueItems = this.itemDaoImpl.getAll();
		
		//then
		assertTrue(allCatalogueItems.contains(newMockItem));
	}
	
	@Test
	public void whenAddAFollowerisCalledOnACatalogueItemsTheNumberOfFollowersForThisCatalogueItemShouldBeIncremented(){
		//given
		List<User> itemFollowers = new ArrayList<User>(); 
		when(mockItem.getFollowers()).thenReturn(itemFollowers);
		int numberOfFollowersBeforeAddingAFollower = itemFollowers.size();
		
		//when
		this.itemDaoImpl.addFollower(mockItem, mockUser);
		
		//then
		int numberOfFollowersAfterAddingAFollower = itemFollowers.size();
		assertEquals(numberOfFollowersBeforeAddingAFollower+1, numberOfFollowersAfterAddingAFollower);
	}
	
	@Test
	public void whenGetAllFollowersisCalledOnACatalogueItemsItShouldReturnAllFollowersForThatItem(){
		//given
		List<User> itemFollowers = new ArrayList<User>(); 
		when(mockItem.getFollowers()).thenReturn(itemFollowers);
		
		//when
		mockItem.getFollowers().add(mockUser);
		
		//then
		assertTrue(itemDaoImpl.getAllFollowers(mockItem).contains(mockUser));
	}
	
	
	
	
	
	
	

}

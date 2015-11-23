package org.crowdlib.daos;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.crowdlib.entities.CatalogueItem;
import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;
import org.crowdlib.exceptions.CatalogueItemNotFoundException;
import org.crowdlib.exceptions.CommentNotFoundException;
import org.crowdlib.inmemory.collections.InMemoryCatalogueItemCollection;

public class CatalogueItemDaoImpl implements CatalogueItemDao {

	@Override
	public CatalogueItem getById(Integer itemId) throws CatalogueItemNotFoundException {
		CatalogueItem item = InMemoryCatalogueItemCollection.getCatalogueItem(itemId);
		if(item == null) throw new CatalogueItemNotFoundException("Catalogue item not available in the system");
		return item;
	}

	@Override
	public List<Comment> getAllComments(CatalogueItem item) {
		return item.getComments();
	}

	@Override
	public List<Comment> getSubsetOfComments(CatalogueItem item, Integer startIndex, Integer size) {
		List<Comment> allComments = item.getComments();
		if(startIndex > allComments.size())
			return new ArrayList<Comment>();
		else if (startIndex + size > allComments.size())
			return allComments.subList(startIndex, allComments.size());
		else
			return allComments.subList(startIndex, startIndex + size);

	}
	
	@Override
	public int getNumberOfRemainingComments(CatalogueItem item, Integer newStartIndex) {
		List<Comment> allComments = item.getComments();
		if(newStartIndex >= allComments.size())
			return 0;
		else 
			return allComments.size() - newStartIndex;
	}

	@Override
	public void addComment(CatalogueItem item, Comment comment, Link linkToComment) {
		item.getComments().add(comment);
		item.getLinksToEachComment().add(linkToComment);
	}

	@Override
	public List<CatalogueItem> getAll() {
		return InMemoryCatalogueItemCollection.getAllCatalogueItems();
	}

	@Override
	public void addFollower(CatalogueItem item, User follower) {
		item.getFollowers().add(follower);
	}

	@Override
	public List<User> getAllFollowers(CatalogueItem item) {
		return item.getFollowers();
		
	}

}

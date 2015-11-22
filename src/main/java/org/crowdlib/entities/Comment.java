package org.crowdlib.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Comment {

	private Integer id;

	private String commentContent;

	private LocalDateTime creationTimestamp;
	
	private User owner;
	
	private CatalogueItem catalogueItem;
	
	private Comment parentComment;
	
	private List<Comment> replies;
	
	private Link linkToSelf;
	
	private Link linkToReplies;

	private int favouritesCount;
	
	private static int numberOfComments=0;

	public Comment(String content, User owner, CatalogueItem catalogueItem) {
		super();
		this.id = ++numberOfComments;
		this.commentContent = content;
		this.creationTimestamp = LocalDateTime.now();
		this.owner = owner;
		this.catalogueItem = catalogueItem;
		this.parentComment = null;
		this.replies = new ArrayList<Comment>();
		this.favouritesCount = 0;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public LocalDateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(LocalDateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@XmlTransient
	public CatalogueItem getCatalogueItem() {
		return catalogueItem;
	}

	public void setCatalogueItem(CatalogueItem catalogueItem) {
		this.catalogueItem = catalogueItem;
	}

	@XmlTransient
	public Comment getParentComment() {
		return parentComment;
	}

	public void setParentComment(Comment parentComment) {
		this.parentComment = parentComment;
	}

	@XmlTransient
	public List<Comment> getReplies() {
		return replies;
	}

	public void setReplies(List<Comment> replies) {
		this.replies = replies;
	}

    @XmlElement(name = "linkToSelf")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class) 
	public Link getLinkToSelf() {
		return linkToSelf;
	}

	public void setLinkToSelf(Link linkToSelf) {
		this.linkToSelf = linkToSelf;
	}
	
    @XmlElement(name = "linkToReplies")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class) 
	public Link getLinkToReplies() {
		return linkToReplies;
	}

	public void setLinkToReplies(Link linkToReplies) {
		this.linkToReplies = linkToReplies;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}
	
	
	
}

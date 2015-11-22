package org.crowdlib.entities;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Notification {
	
	private CatalogueItem catalogueItem;
	
	private Comment comment;
	
	private Link linkToComment;

	public Notification(CatalogueItem catalogueItem, Comment comment, Link linkToComment) {
		super();
		this.catalogueItem = catalogueItem;
		this.comment = comment;
		this.linkToComment = linkToComment;
	}

	@XmlTransient
	public CatalogueItem getCatalogueItem() {
		return catalogueItem;
	}

	public void setCatalogueItem(CatalogueItem catalogueItem) {
		this.catalogueItem = catalogueItem;
	}

	@XmlTransient
	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

    @XmlElement(name = "linkToNewComment")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class) 
	public Link getLinkToComment() {
		return linkToComment;
	}

	public void setLinkToComment(Link linkToComment) {
		this.linkToComment = linkToComment;
	}
	
	
}

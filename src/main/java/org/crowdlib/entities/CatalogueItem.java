package org.crowdlib.entities;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class CatalogueItem {

	private Integer id;

	private String title;
	
	private String author;
	
	private List<Comment> comments;
	
	private List<User> followers;
	
	private Link linkToAllComments;

	private List<Link> linksToEachComment;
	
	public CatalogueItem(){
		
	}

	public CatalogueItem(Integer id, String title, String author, Link linkToAllComments) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.comments = new ArrayList<Comment>();
		this.followers = new ArrayList<User>();
		this.linkToAllComments = linkToAllComments;
		this.linksToEachComment = new ArrayList<Link>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@XmlTransient
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
    @XmlElement(name = "linksToEachComment")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class) 
    public List<Link> getLinksToEachComment() {
        return linksToEachComment;
    }
    
    public void setLinksToEachComment(List<Link> links) {
        this.linksToEachComment = links;
    }
    
	public Link getLinkToAllComments() {
		return linkToAllComments;
	}

	public void setLinkToAllComments(Link linkToAllComments) {
		this.linkToAllComments = linkToAllComments;
	}

    @XmlTransient
	public List<User> getFollowers() {
		return followers;
	}

	public void setFollowers(List<User> followers) {
		this.followers = followers;
	}
    
    
	
}

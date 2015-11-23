/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.crowdlib.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class User {

	private Integer id;

	private String title;

	private String name;

	private String surname;
	
	private String role;

	private String username;

	private String password;

	private List<Comment> favouriteComments;
	
	private List<CatalogueItem> followedItems;
	
	private List<Notification> notifications;

	public User() {
		this.favouriteComments = new ArrayList<Comment>();
		this.followedItems = new ArrayList<CatalogueItem>();
		this.notifications = new ArrayList<Notification>();
	}
	
	public User(String username) {
		this.username = username;
		this.favouriteComments = new ArrayList<Comment>();
		this.followedItems = new ArrayList<CatalogueItem>();
		this.notifications = new ArrayList<Notification>();
	}

	public User(Integer id, String title, String name, String surname, String role, String username, String password) {
		this.id = id;
		this.title = title;
		this.name = name;
		this.surname = surname;
		this.role = role;
		this.username = username;
		this.password = password;
		this.favouriteComments = new ArrayList<Comment>();
		this.followedItems = new ArrayList<CatalogueItem>();
		this.notifications = new ArrayList<Notification>();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlTransient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlTransient
	public List<Comment> getFavouriteComments() {
		return favouriteComments;
	}

	public void setFavouriteComments(List<Comment> comments) {
		this.favouriteComments = comments;
	}

	@XmlTransient
	public List<CatalogueItem> getFollowedItems() {
		return followedItems;
	}

	public void setFollowedItems(List<CatalogueItem> followedItems) {
		this.followedItems = followedItems;
	}
	
	@XmlTransient
	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof User)) {
			return false;
		}
		User other = (User) object;
		if (this.id == other.id)
			return true;
		else
			return false;
	}

}

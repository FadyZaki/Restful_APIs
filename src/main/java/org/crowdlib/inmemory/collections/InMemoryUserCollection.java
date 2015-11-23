package org.crowdlib.inmemory.collections;

import java.util.HashMap;

import org.crowdlib.entities.RoleTypeConstants;
import org.crowdlib.entities.User;

public class InMemoryUserCollection {
	private static HashMap<String, User> users = new HashMap<String, User>();

	public static void initializeInMemoryUsers(){
		users.put("student1", new User(1, "Mr.", "Fady", "Zaki", RoleTypeConstants.GUEST_USER, "student1", "whoopey"));
		users.put("student2", new User(2, "Mr.", "John", "Doe", RoleTypeConstants.GUEST_USER, "student2", "password"));
		users.put("lecturer", new User(3, "Mr.", "Alex", "Voss", RoleTypeConstants.ADMIN_USER, "lecturer", "secret"));
	}
	
	public static void addUser(User user) {
		users.put(user.getUsername(), user);
	}
	
	public static User getUser(String username) {
		return users.get(username);
	}

}

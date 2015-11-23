package org.crowdlib.inmemory.collections;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.crowdlib.entities.Comment;
import org.crowdlib.entities.User;

public class InMemoryCommentCollection {
	private static HashMap<Integer, Comment> comments = new HashMap<Integer, Comment>();

	public static void initializeInMemoryComments() {
	}

	public static void addComment(Comment comment) {
		comments.put(comment.getId(), comment);
	}

	public static Comment getComment(Integer id) {
		return comments.get(id);
	}

}

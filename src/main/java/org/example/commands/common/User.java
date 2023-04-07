package org.example.commands.common;

import java.util.HashMap;
import java.util.List;

public class User {
	private long userId;
	private String userName;

	private HashMap<Integer, List<Integer>> rolls;

	public User(long userId, String userName) {
		this();
		this.userId = userId;
		this.userName = userName;
	}

	private User() {
		rolls = new HashMap<>();
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public HashMap<Integer, List<Integer>> getRolls() {
		return rolls;
	}

	public void setRolls(HashMap<Integer, List<Integer>> rolls) {
		this.rolls = rolls;
	}
}

package de.schwarz.diceroller.commands.common;

import java.util.ArrayList;
import java.util.List;

public class User {
	private long userId;
	private String userName;

	private final List<DiceResult> diceResults;

	public User(long userId, String userName) {
		this();
		this.userId = userId;
		this.userName = userName;
	}

	private User() {
		diceResults = new ArrayList<>();
	}

	public long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public List<DiceResult> getDiceResults() {
		return diceResults;
	}
}

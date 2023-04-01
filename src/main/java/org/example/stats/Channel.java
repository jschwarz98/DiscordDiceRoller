package org.example.stats;

import java.util.ArrayList;
import java.util.List;

public class Channel {
	private final long channelId;
	private final List<User> userList;

	public Channel(long channelId) {
		this(channelId, new ArrayList<>());
	}

	public Channel(long channelId, List<User> userList) {
		this.channelId = channelId;
		this.userList = userList;
	}

	public long getChannelId() {
		return channelId;
	}

	public List<User> getUserList() {
		return userList;
	}
}

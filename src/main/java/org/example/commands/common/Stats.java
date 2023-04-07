package org.example.commands.common;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stats {

	public static List<Channel> trackedChannels = new ArrayList<>();

	public static void track(MessageChannelUnion channel, User author, List<Tuple<Integer, List<Integer>>> results) {

		Channel c = trackedChannels.stream().filter(ch -> ch.getChannelId() == channel.getIdLong()).findFirst().orElse(null);
		if (c == null) return;

		org.example.commands.common.User user = c.getUserList().stream().filter(u -> u.getUserId() == author.getIdLong()).findFirst().orElse(null);
		if (user == null) {
			user = new org.example.commands.common.User(author.getIdLong(), author.getName());
			c.getUserList().add(user);
		}

		HashMap<Integer, List<Integer>> userRollMap = user.getRolls();
		for (Tuple<Integer, List<Integer>> result : results) {
			userRollMap.compute(result.getOne(), (key, val) -> {
				if (val == null) val = new ArrayList<>();
				val.addAll(result.getTwo());
				return val;
			});
		}
	}
}

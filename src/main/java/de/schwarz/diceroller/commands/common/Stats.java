package de.schwarz.diceroller.commands.common;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.ArrayList;
import java.util.List;

public class Stats {

	public static List<Channel> trackedChannels = new ArrayList<>();

	public static boolean channelIsTracked(long id) {
		return trackedChannels.stream().anyMatch(c -> c.getChannelId() == id);
	}

	public static boolean removeChannel(long channelId) {
		return Stats.trackedChannels.removeIf(channel -> channel.getChannelId() == channelId);
	}

	public static boolean addChannel(long channelId) {
		if (channelIsTracked(channelId)) {
			return false;
		}
		trackedChannels.add(new Channel(channelId));
		return true;
	}

	public static void track(MessageChannelUnion channel, User author, List<DiceResult> results) {

		Channel c = trackedChannels.stream().filter(ch -> ch.getChannelId() == channel.getIdLong()).findFirst().orElse(null);
		if (c == null) return;

		de.schwarz.diceroller.commands.common.User user = c.getUserList().stream().filter(u -> u.getUserId() == author.getIdLong()).findFirst().orElse(null);
		if (user == null) {
			user = new de.schwarz.diceroller.commands.common.User(author.getIdLong(), author.getName());
			c.getUserList().add(user);
		}

		List<DiceResult> userDiceResults = user.getDiceResults();
		for (DiceResult result : results) {
			if (userDiceResults
					.stream()
					.anyMatch(dr -> dr.getDie().equals(result.getDie()))) {

				DiceResult userDiceResult = userDiceResults
						.stream()
						.filter(udr -> udr.getDie().equals(result.getDie()))
						.findFirst()
						.orElseThrow();
				userDiceResult.getResultList().getResults().addAll(result.getResultList().getResults());
			} else {
				userDiceResults.add(result);
			}
		}
	}
}

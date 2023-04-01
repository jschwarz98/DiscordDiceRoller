package org.example.stats.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.TextCommandHandler;
import org.example.stats.Channel;
import org.example.stats.Stats;

public class TrackChannelCommand implements TextCommandHandler {

	@Override
	public void accept(MessageReceivedEvent event) {

		if (!event.getMessage().getContentDisplay().startsWith("!track")) return;

		long cid = event.getChannel().getIdLong();

		if (Stats.trackedChannels.stream().anyMatch(c -> c.getChannelId() == cid)) {
			event.getMessage().reply("This channel is already being tracked! To see the collected information use the following commands: '!stats'; '!stat --user'").queue();
			return;
		}

		Stats.trackedChannels.add(new Channel(cid));
		event.getMessage().reply("Tracking this channel from now on. To see the collected information use the following commands: '!stats'; '!stat --user'").queue();
	}
}

package org.example.stats.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.TextCommandHandler;
import org.example.stats.Stats;

public class StopTrackChannelCommand implements TextCommandHandler {

	@Override
	public void accept(MessageReceivedEvent event) {

		if (!event.getMessage().getContentDisplay().startsWith("!stop")) return;

		long cid = event.getChannel().getIdLong();

		boolean removedAny = Stats.trackedChannels.removeIf(channel -> channel.getChannelId() == cid);
		if (removedAny) {
			event.getMessage().reply("This channel is no longer being tracked!").queue();
		}
	}
}

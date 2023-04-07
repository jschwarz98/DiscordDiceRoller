package org.example.commands.stats;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.TextCommandHandler;
import org.example.commands.common.Stats;
import org.example.commands.common.messages.AbstractMessageData;
import org.example.commands.common.messages.ReplyData;

public class StopTrackChannelCommand implements TextCommandHandler {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		long cid = event.getChannel().getIdLong();

		boolean removedAny = Stats.trackedChannels.removeIf(channel -> channel.getChannelId() == cid);
		if (removedAny) {
			return new ReplyData("This channel is no longer being tracked!");
		}
		return null;
	}
}

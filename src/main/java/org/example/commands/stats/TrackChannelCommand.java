package org.example.commands.stats;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.TextCommandHandler;
import org.example.commands.common.Channel;
import org.example.commands.common.Stats;
import org.example.commands.common.messages.AbstractMessageData;
import org.example.commands.common.messages.ReplyData;

public class TrackChannelCommand implements TextCommandHandler {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		long cid = event.getChannel().getIdLong();
		String usageInfo = "To see the collected information use the following commands: !stats; !stats --user";

		if (Stats.trackedChannels.stream().anyMatch(c -> c.getChannelId() == cid)) {
			return new ReplyData("This channel is already being tracked! " + usageInfo);
		}

		Stats.trackedChannels.add(new Channel(cid));
		return new ReplyData("Tracking this channel from now on. " + usageInfo);
	}
}

package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.common.Channel;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

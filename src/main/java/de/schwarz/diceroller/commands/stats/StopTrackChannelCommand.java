package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

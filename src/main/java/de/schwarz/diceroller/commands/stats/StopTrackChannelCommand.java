package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.buttons.StatButtons;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopTrackChannelCommand implements TextCommandHandler {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		return stopTracking(event.getChannel().getIdLong());
	}

	public AbstractMessageData stopTracking(long channelId) {
		ReplyData data = new ReplyData();
		data.setActionRows(StatButtons.getStatButtons(false));

		if (Stats.removeChannel(channelId)) {
			data.setContent("This channel is no longer being tracked!");
			return data;
		}
		data.setContent("This channel is not being tracked! Start tracking it by pressing the button below!");
		return data;
	}
}

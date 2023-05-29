package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.buttons.RollButtons;
import de.schwarz.diceroller.commands.buttons.StatButtons;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TrackChannelCommand implements TextCommandHandler {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		return startTracking(event.getChannel().getIdLong());
	}

	public AbstractMessageData startTracking(long channelId) {
		String usageInfo = "To see the collected information use the following commands: !stats; !stats --user";
		ReplyData replyData = new ReplyData();

		replyData.setActionRows(RollButtons.getRollButtons_4_6_8(),
				RollButtons.getRollButtons_10_12_20(),
				StatButtons.getStatButtons(true));

		if (!Stats.addChannel(channelId)) {
			replyData.setContent("This channel is already being tracked! " + usageInfo);
			return replyData;
		}

		replyData.setContent("Tracking this channel from now on. " + usageInfo);
		return replyData;
	}

}

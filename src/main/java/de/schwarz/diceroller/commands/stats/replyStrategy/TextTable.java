package de.schwarz.diceroller.commands.stats.replyStrategy;

import de.schwarz.diceroller.commands.common.AggregatedDiceResult;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class TextTable implements ReplyStrategy {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event, String title, List<AggregatedDiceResult> datasets) {
		return new ReplyData("Not implemented yet...");
	}

}

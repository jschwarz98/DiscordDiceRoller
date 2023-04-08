package de.schwarz.diceroller.commands.stats.replyStrategy;

import de.schwarz.diceroller.commands.common.Tuple;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class TextTable implements ReplyStrategy {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event, String title, List<Tuple<Integer, List<Tuple<Integer, Integer>>>> datasets) {
		return new ReplyData("Not implemented yet...");
	}

}

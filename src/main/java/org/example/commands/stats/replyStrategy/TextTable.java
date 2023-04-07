package org.example.commands.stats.replyStrategy;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.common.Tuple;
import org.example.commands.common.messages.AbstractMessageData;
import org.example.commands.common.messages.ReplyData;

import java.util.List;

public class TextTable implements ReplyStrategy {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event, String title, List<Tuple<Integer, List<Tuple<Integer, Integer>>>> datasets) {
		return new ReplyData("Not implemented yet...");
	}

}

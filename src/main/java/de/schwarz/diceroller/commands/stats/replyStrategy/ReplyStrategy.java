package de.schwarz.diceroller.commands.stats.replyStrategy;

import de.schwarz.diceroller.commands.common.Tuple;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@FunctionalInterface
public interface ReplyStrategy {

	/**
	 * @param event    the event to answer
	 * @param datasets a list of tuples containing the specific die (W20) and a list of tuples of the result and the count of the result, e.g. 4, 6 => rolled number 6 4 times
	 */
	AbstractMessageData accept(MessageReceivedEvent event, String title, List<Tuple<Integer, List<Tuple<Integer, Integer>>>> datasets);

}

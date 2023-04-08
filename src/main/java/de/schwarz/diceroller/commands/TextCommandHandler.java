package de.schwarz.diceroller.commands;

import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@FunctionalInterface
public interface TextCommandHandler {
	AbstractMessageData accept(MessageReceivedEvent event);
}

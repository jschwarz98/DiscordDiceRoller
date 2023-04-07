package org.example.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.common.messages.AbstractMessageData;

@FunctionalInterface
public interface TextCommandHandler {
	AbstractMessageData accept(MessageReceivedEvent event);
}

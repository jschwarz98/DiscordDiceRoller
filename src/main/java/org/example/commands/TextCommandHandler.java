package org.example.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@FunctionalInterface
public interface TextCommandHandler {
	void accept(MessageReceivedEvent event);
}

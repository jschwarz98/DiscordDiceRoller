package org.example.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandlerBroker extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		TextCommandHandler handler = CommandHandler.getHandlerFor(event.getMessage().getContentDisplay());
		if (handler == null)
			return;
		handler.accept(event);
	}

}

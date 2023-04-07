package org.example.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.example.commands.common.messages.AbstractMessageData;

public class CommandHandlerBroker extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		TextCommandHandler handler = CommandHandler.getHandlerFor(event.getMessage().getContentDisplay());
		if (handler == null)
			return;

		AbstractMessageData message = handler.accept(event);

		if (message == null)
			return;

		MessageCreateAction action = switch (message.getType()) {
			case REPLY -> event.getMessage().reply("");
			case MESSAGE -> event.getChannel().sendMessage("");
		};
		action.setContent(message.getContent());
		if (message.getEmbeds() != null && message.getEmbeds().length < 10)
			action.addEmbeds(message.getEmbeds());
		if (message.getFiles() != null)
			action.addFiles(message.getFiles());

		action.queue();
	}

}

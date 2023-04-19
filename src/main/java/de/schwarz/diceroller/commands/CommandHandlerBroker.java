package de.schwarz.diceroller.commands;

import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class CommandHandlerBroker extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		TextCommandHandler handler = CommandHandler.getHandlerForMessage(event.getMessage().getContentDisplay());
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

		if (message.getActionRows() != null) {
			for (ActionRow actionRow : message.getActionRows()) {
				action.addActionRow(actionRow.getComponents());
			}
		}

		action.queue();
	}

}

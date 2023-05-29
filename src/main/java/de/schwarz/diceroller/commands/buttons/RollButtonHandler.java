package de.schwarz.diceroller.commands.buttons;

import de.schwarz.diceroller.commands.common.messages.ReplyData;
import de.schwarz.diceroller.commands.rolling.RollDiceCommand;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.Arrays;

public class RollButtonHandler extends ListenerAdapter {

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		ButtonInteraction interaction = event.getInteraction();
		String buttonId = interaction.getButton().getId();

		RollButton button = Arrays.stream(RollButton.values()).filter(b -> b.id.equals(buttonId)).findFirst().orElse(null);
		if (button == null) return;

		ReplyData replyData = new RollDiceCommand().performRoll(event.getChannel(), event.getUser(), button.label);

		String userName = event.getMember() != null ? event.getMember().getEffectiveName() : event.getUser().getName();

		ReplyCallbackAction action = event.reply("");
		action.setContent(
				"For **" + userName + "**" + System.lineSeparator()
						+ System.lineSeparator()
						+ replyData.getContent()
						+ System.lineSeparator()
						+ System.lineSeparator()
						+ "Lets hope the Dice-Gods are on your side!"
		);

		if (replyData.getEmbeds() != null && replyData.getEmbeds().length < 10)
			action.addEmbeds(replyData.getEmbeds());
		if (replyData.getFiles() != null)
			action.addFiles(replyData.getFiles());

		if (replyData.getActionRows() != null) {
			for (ActionRow actionRow : replyData.getActionRows()) {
				action.addActionRow(actionRow.getComponents());
			}
		}

		action.queue();
	}

}


package de.schwarz.diceroller.commands.buttons;

import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.stats.GetStatsCommand;
import de.schwarz.diceroller.commands.stats.StopTrackChannelCommand;
import de.schwarz.diceroller.commands.stats.TrackChannelCommand;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class StatButtonHandler extends ListenerAdapter {

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		StatButton button = Arrays.stream(StatButton.values())
				.filter(b -> b.id.equals(event.getButton().getId()))
				.findFirst()
				.orElse(null);

		if (button == null) return;

		AbstractMessageData replyData = switch (button) {
			case START_TRACKING -> new TrackChannelCommand().startTracking(event.getChannel().getIdLong());
			case STOP_TRACKING -> new StopTrackChannelCommand().stopTracking(event.getChannel().getIdLong());
			case CHANNEL_STATS ->
					new GetStatsCommand().createStatsReply(event.getChannel().getIdLong(), event.getUser(), "");
			case USER_STATS ->
					new GetStatsCommand().createStatsReply(event.getChannel().getIdLong(), event.getUser(), GetStatsCommand.USER_STAT_FLAG);
		};


		ReplyCallbackAction action = event.reply(replyData.getContent());

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

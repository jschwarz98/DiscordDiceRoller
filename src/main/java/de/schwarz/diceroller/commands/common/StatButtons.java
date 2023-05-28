package de.schwarz.diceroller.commands.common;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class StatButtons {

	public static ActionRow getStatButtons(boolean channelIsTracked) {

		if (!channelIsTracked) {
			return ActionRow.of(Button.primary(StatButton.START_TRACKING.id, StatButton.START_TRACKING.label));
		}

		return ActionRow.of(
				Button.primary(StatButton.CHANNEL_STATS.id, StatButton.CHANNEL_STATS.label),
				Button.primary(StatButton.USER_STATS.id, StatButton.USER_STATS.label),
				Button.primary(StatButton.STOP_TRACKING.id, StatButton.STOP_TRACKING.label)
		);

	}

}

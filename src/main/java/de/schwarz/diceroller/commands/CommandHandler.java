package de.schwarz.diceroller.commands;

import de.schwarz.diceroller.commands.rolling.RollDiceCommand;
import de.schwarz.diceroller.commands.stats.GetStatsCommand;
import de.schwarz.diceroller.commands.stats.StopTrackChannelCommand;
import de.schwarz.diceroller.commands.stats.TrackChannelCommand;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum CommandHandler {

	ROLL_DICE(new RollDiceCommand(), "!roll"),
	TRACK_CHANNEL(new TrackChannelCommand(), "!track"),
	STOP_TRACK_CHANNEL(new StopTrackChannelCommand(), "!stop"),
	STATS(new GetStatsCommand(), "!stats");


	private final TextCommandHandler handler;
	private final String prefix;

	CommandHandler(TextCommandHandler handler, String prefix) {
		this.handler = handler;
		this.prefix = prefix;
	}

	@Nullable
	public static TextCommandHandler getHandlerForMessage(String message) {
		return Arrays.stream(CommandHandler.values())
				.filter(h -> message.startsWith(h.prefix))
				.map(CommandHandler::getHandler)
				.findFirst()
				.orElse(null);
	}

	public String getPrefix() {
		return prefix;
	}

	public TextCommandHandler getHandler() {
		return handler;
	}
}

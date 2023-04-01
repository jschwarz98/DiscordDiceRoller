package org.example.commands;

import org.example.rolling.RollDiceCommand;
import org.example.stats.commands.GetStatsCommand;
import org.example.stats.commands.StopTrackChannelCommand;
import org.example.stats.commands.TrackChannelCommand;
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
	public static TextCommandHandler getHandlerFor(String message) {
		return Arrays.stream(CommandHandler.values())
				.filter(h -> message.startsWith(h.prefix))
				.map(CommandHandler::getHandlerFor)
				.findFirst()
				.orElse(null);
	}

	public TextCommandHandler getHandlerFor() {
		return handler;
	}
}

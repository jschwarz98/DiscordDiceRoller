package de.schwarz.diceroller.commands.common;

public enum StatButton {

	CHANNEL_STATS("channel_stats", "Stats"),
	USER_STATS("user_stats", "User-Stats"),
	STOP_TRACKING("stop_tracking", "Stop Tracking this channel"),
	START_TRACKING("start_tracking", "Start Tracking this channel");

	public final String id;
	public final String label;

	StatButton(String id, String label) {
		this.id = id;
		this.label = label;
	}

}

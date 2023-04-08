package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.common.Defaults;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrackChannelCommandTest {

	private static TextCommandHandler handler;

	@BeforeAll
	public static void setUp() {
		handler = new TrackChannelCommand();
	}

	@BeforeEach
	public void setUpStats() {
		Stats.trackedChannels = new ArrayList<>();
	}

	@Test
	public void track_untrackedChannel() {
		MessageReceivedEvent mockMessageEvent = Defaults.createMockMessageEvent("!track", 123L);
		AbstractMessageData reply1 = handler.accept(mockMessageEvent);
		AbstractMessageData reply2 = handler.accept(Defaults.createMockMessageEvent("!track", 124L));

		assertTrue(reply1.getContent().contains("Tracking this channel from now on"));
		assertEquals(reply1.getContent(), reply2.getContent());

		assertEquals(2, Stats.trackedChannels.size());
	}

	@Test
	public void track_alreadyTrackedChannel() {
		AbstractMessageData reply1 = handler.accept(Defaults.createMockMessageEvent("!track", 123L));
		AbstractMessageData reply2 = handler.accept(Defaults.createMockMessageEvent("!track", 123L));

		assertTrue(reply1.getContent().contains("Tracking this channel from now on"));
		Assertions.assertNotEquals(reply1.getContent(), reply2.getContent());
		Assertions.assertTrue(reply2.getContent().startsWith("This channel is already being tracked!"));
		assertEquals(1, Stats.trackedChannels.size());
	}

}
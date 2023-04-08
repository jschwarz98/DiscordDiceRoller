package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.common.Channel;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.common.Defaults;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StopTrackChannelCommandTest {

	private static TextCommandHandler handler;

	@BeforeAll
	public static void setUp() {
		handler = new StopTrackChannelCommand();
	}

	@BeforeEach
	public void setUpStats() {
		Stats.trackedChannels = new ArrayList<>();
	}

	@Test
	public void stopTracking_emptyChannelList() {
		MessageReceivedEvent mockMessageEvent = Defaults.createMockMessageEvent("!stop", 123L);
		AbstractMessageData reply1 = handler.accept(mockMessageEvent);
		assertNull(reply1);
	}

	@Test
	public void stopTracking_filledChannelList() {
		Stats.trackedChannels.add(new Channel(123L));
		Stats.trackedChannels.add(new Channel(124L));
		Stats.trackedChannels.add(new Channel(125L));

		MessageReceivedEvent mockMessageEvent = Defaults.createMockMessageEvent("!stop", 123L);
		AbstractMessageData reply1 = handler.accept(mockMessageEvent);
		assertNotNull(reply1);
		assertEquals(2, Stats.trackedChannels.size());
	}

	@Test
	public void stopTracking_filledChannelList_butChannelNotInList() {
		Stats.trackedChannels.add(new Channel(124L));
		Stats.trackedChannels.add(new Channel(125L));
		Stats.trackedChannels.add(new Channel(126L));

		MessageReceivedEvent mockMessageEvent = Defaults.createMockMessageEvent("!stop", 123L);
		AbstractMessageData reply1 = handler.accept(mockMessageEvent);
		assertNull(reply1);
		assertEquals(3, Stats.trackedChannels.size());
	}

}
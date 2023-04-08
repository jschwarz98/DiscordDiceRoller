package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.common.*;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import de.schwarz.diceroller.commands.stats.chart.ChartConfig;
import de.schwarz.diceroller.commands.stats.replyStrategy.GraphVisualization;
import de.schwarz.diceroller.commands.stats.replyStrategy.ReplyStrategy;
import de.schwarz.diceroller.commands.stats.replyStrategy.TextTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GetStatsCommandTest {

	private static GetStatsCommand handler;

	// TODO testing...
	@BeforeAll
	public static void setUp() {
		handler = new GetStatsCommand();
	}

	@BeforeEach
	public void clearStats() {
		Stats.trackedChannels = new ArrayList<>();
	}

	private Channel createTestChannel() {
		Channel channel = new Channel(123L);
		User user = new User(123, "testUser");
		User user2 = new User(1234, "testUser2");

		channel.getUserList().add(user);
		channel.getUserList().add(user2);

		Stats.trackedChannels.add(channel);
		Stats.track(
				Defaults.createMockChannel(),
				Defaults.createMockAuthor(),
				Arrays.asList(
						new Tuple<>(4, Arrays.asList(1, 2, 3, 4)),
						new Tuple<>(20, Arrays.asList(10, 11, 12))
				)
		);

		return channel;
	}

	@Test
	void generateDataSets() {
		User[] users = createTestChannel()
				.getUserList()
				.toArray(new User[0]);
		List<Tuple<Integer, List<Tuple<Integer, Integer>>>> tuples = handler.generateDataSets(users);
		assertEquals(2, tuples.size());

		Tuple<Integer, List<Tuple<Integer, Integer>>> w4 = tuples.get(0);
		assertEquals(4, w4.getTwo().size());
		List<Tuple<Integer, Integer>> w4Results = w4.getTwo();
		for (int i = 0; i < w4Results.size(); i++) {
			Tuple<Integer, Integer> resultPairing = w4Results.get(i);
			assertEquals(i + 1, resultPairing.getOne());
			assertEquals(1, resultPairing.getTwo());
		}

		Tuple<Integer, List<Tuple<Integer, Integer>>> w20 = tuples.get(1);
		assertEquals(3, w20.getTwo().size());
		List<Tuple<Integer, Integer>> two = w20.getTwo();
		for (int i = 0; i < two.size(); i++) {
			Tuple<Integer, Integer> resultPairing = w4Results.get(i);
			assertEquals(i + 1, resultPairing.getOne());
			assertEquals(1, resultPairing.getTwo());
		}
	}

	@Test
	void accept() {
		// no tracked channels...
		AbstractMessageData replyData = handler.accept(Defaults.createMockMessageEvent("!stats"));
		assertNull(replyData);

		// add tracked channel
		Stats.trackedChannels.add(createTestChannel());
		replyData = handler.accept(Defaults.createMockMessageEvent("!stats"));
		assertSame(replyData.getClass(), ReplyData.class);
		assertEquals("Here you go~", replyData.getContent());
		assertEquals(1, replyData.getFiles().length);
		assertNotNull(replyData.getFiles()[0]);

		replyData = handler.accept(Defaults.createMockMessageEvent("!stats --user"));
		assertSame(replyData.getClass(), ReplyData.class);
		assertEquals("Here you go~", replyData.getContent());
		assertEquals(1, replyData.getFiles().length);
		assertNotNull(replyData.getFiles()[0]);

		replyData = handler.accept(Defaults.createMockMessageEvent("!stats --user --table"));
		assertSame(replyData.getClass(), ReplyData.class);
		assertEquals("Not implemented yet...", replyData.getContent());
		assertNull(replyData.getFiles());
	}


	@Test
	void getReplyStrategy() {
		ReplyStrategy strategy = handler.getReplyStrategy("!stats --user --table");
		assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats --table");
		assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats --table asdasd");
		assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats --tableasdasd");
		assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats");
		assertSame(strategy.getClass(), GraphVisualization.class);

		strategy = handler.getReplyStrategy("!stats--table");
		assertSame(strategy.getClass(), GraphVisualization.class);

	}

	@Test
	void createChartConfig() {
		Channel testChannel = createTestChannel();
		Optional<ChartConfig> config = handler.createChartConfig("!stats", Defaults.createMockAuthor(), testChannel);
		if (config.isEmpty()) Assertions.fail();
		assertEquals(2, config.get().users().length);

		Optional<ChartConfig> userConfig = handler.createChartConfig("!stats --user", Defaults.createMockAuthor(), testChannel);
		if (userConfig.isEmpty()) Assertions.fail();
		assertEquals(1, userConfig.get().users().length);
		assertEquals("testUser", userConfig.get().users()[0].getUserName());

	}

}
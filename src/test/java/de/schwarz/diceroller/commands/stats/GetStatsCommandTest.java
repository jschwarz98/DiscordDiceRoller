package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.common.*;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import de.schwarz.diceroller.commands.stats.chart.ChartConfig;
import de.schwarz.diceroller.commands.stats.replyStrategy.GraphVisualization;
import de.schwarz.diceroller.commands.stats.replyStrategy.ReplyStrategy;
import de.schwarz.diceroller.commands.stats.replyStrategy.TextTable;
import de.schwarz.diceroller.common.Defaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

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

		List<DiceResult> results = new ArrayList<>();
		results.add(new DiceResult(new Dice(4)));
		results.get(0).getResultList().getResults().addAll(Arrays.asList(1, 2, 3, 4));
		results.add(new DiceResult(new Dice(20)));
		results.get(1).getResultList().getResults().addAll(Arrays.asList(10, 11, 12));

		Stats.track(
				Defaults.createMockChannel(),
				Defaults.createMockAuthor(),
				results
		);

		return channel;
	}

	@Test
	void generateDataSets() {
		User[] users = createTestChannel()
				.getUserList()
				.toArray(new User[0]);
		List<AggregatedDiceResult> adr = handler.generateDataSets(users);
		assertEquals(2, adr.size());

		Map<Integer, Integer> w4 = adr.get(0).getAggregatedDiceResultList().getMap();
		assertEquals(4, w4.size());
		for (int i = 0; i < w4.size(); i++) {
			assertEquals(1, w4.get(i + 1));
		}

		Map<Integer, Integer> w20 = adr.get(1).getAggregatedDiceResultList().getMap();
		assertEquals(3, w20.size());
		for (int i = 10; i < 10 + w20.size(); i++) {
			assertEquals(1, w20.get(i));
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
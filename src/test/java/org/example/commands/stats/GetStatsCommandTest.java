package org.example.commands.stats;

import org.example.commands.common.Channel;
import org.example.commands.common.User;
import org.example.commands.stats.replyStrategy.GraphVisualization;
import org.example.commands.stats.replyStrategy.ReplyStrategy;
import org.example.commands.stats.replyStrategy.TextTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GetStatsCommandTest {

	private static GetStatsCommand handler;

	// TODO testing...
	@BeforeAll
	public static void setUp() {
		handler = new GetStatsCommand();
	}

	private Channel createTestChannel() {
		Channel channel = new Channel(123L);
		channel.getUserList().add(new User(123, "testUser"));
		return channel;
	}

	@Test
	void generateDataSets() {
	}

	@Test
	void accept() {
	}

	@Test
	void getReplyStrategy() {
		ReplyStrategy strategy = handler.getReplyStrategy("!stats --user --table");
		Assertions.assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats --table");
		Assertions.assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats --table asdasd");
		Assertions.assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats --tableasdasd");
		Assertions.assertSame(strategy.getClass(), TextTable.class);

		strategy = handler.getReplyStrategy("!stats");
		Assertions.assertSame(strategy.getClass(), GraphVisualization.class);

		strategy = handler.getReplyStrategy("!stats--table");
		Assertions.assertSame(strategy.getClass(), GraphVisualization.class);

	}

	@Test
	void createChartConfig() {
	}

}
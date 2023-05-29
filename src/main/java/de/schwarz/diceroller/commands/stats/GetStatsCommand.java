package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.buttons.RollButtons;
import de.schwarz.diceroller.commands.common.*;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import de.schwarz.diceroller.commands.stats.chart.ChartConfig;
import de.schwarz.diceroller.commands.stats.chart.DataSet;
import de.schwarz.diceroller.commands.stats.statReplyStrategy.GraphVisualization;
import de.schwarz.diceroller.commands.stats.statReplyStrategy.StatReplyStrategy;
import de.schwarz.diceroller.commands.stats.statReplyStrategy.TextTable;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class GetStatsCommand implements TextCommandHandler {

	public static final String USER_STAT_FLAG = " --user";

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		return createStatsReply(event.getChannel().getIdLong(), event.getAuthor(), event.getMessage().getContentDisplay());
	}

	public AbstractMessageData createStatsReply(long channelId, net.dv8tion.jda.api.entities.User user, String msg) {
		de.schwarz.diceroller.commands.common.Channel channel = Stats.trackedChannels.stream().filter(c -> c.getChannelId() == channelId).findFirst().orElse(null);
		if (channel == null) return null;

		Optional<ChartConfig> chartConfig = createChartConfig(msg, user, channel);
		if (chartConfig.isEmpty()) {
			ReplyData data = new ReplyData("No information for user " + user.getName() + " in *this channel* yet!");
			data.setActionRows(RollButtons.getRollButtons_4_6_8(), RollButtons.getRollButtons_10_12_20());
			return data;
		}
		List<AggregatedDiceResult> datasets = generateDataSets(chartConfig.get().users());

		StatReplyStrategy strategy = getReplyStrategy(msg);
		return strategy.accept(chartConfig.get().title(), datasets);
	}

	protected StatReplyStrategy getReplyStrategy(String contentDisplay) {
		if (contentDisplay.contains(" --table")) {
			return new TextTable();
		}
		return new GraphVisualization();
	}

	protected Optional<ChartConfig> createChartConfig(String msg, net.dv8tion.jda.api.entities.User author, Channel channel) {
		final String baseTitle = "Dice Roll Results Visualized";
		if (msg.contains(USER_STAT_FLAG)) {
			User user = channel.getUserList().stream().filter(trackedUser -> trackedUser.getUserId() == author.getIdLong()).findFirst().orElse(null);
			if (user == null) {
				return Optional.empty();
			}
			return Optional.of(new ChartConfig(baseTitle + " for " + user.getUserName(), new User[]{user}));
		} else {
			return Optional.of(new ChartConfig(baseTitle, channel.getUserList().toArray(new User[0])));
		}
	}

	protected List<AggregatedDiceResult> generateDataSets(User... users) {
		// Dataset erstellen für jeden Die den wir finden! also für jeden w20 oder w8 etc...
		HashMap<Integer, DataSet> dataSetMap = new HashMap<>();

		// alle ergebnisse zusammengetragen

		HashMap<Dice, DiceResultList> map = new HashMap<>();

		for (User user : users) {

			user.getDiceResults().forEach(result -> {
				Dice die = result.getDie();
				List<Integer> results = result.getResultList().getResults();
				DiceResultList diceResultList = map.computeIfAbsent(die, (d) -> new DiceResultList());
				diceResultList.getResults().addAll(results);
			});

		}

		Map<Dice, Map<Integer, Integer>> resultCountingMap = new HashMap<>();

		map.forEach((dice, valueList) ->
				valueList
						.getResults()
						.forEach(value -> resultCountingMap
								.computeIfAbsent(dice, d -> new HashMap<>())
								.compute(value, (key, currentCount) -> currentCount == null ? 1 : currentCount + 1)));

		return resultCountingMap.entrySet().stream()
				.map(es -> new AggregatedDiceResult(es.getKey(), new AggregatedDiceResultList(es.getValue())))
				.sorted(Comparator.comparing(AggregatedDiceResult::getDie))
				.collect(Collectors.toList());
	}
}

package de.schwarz.diceroller.commands.stats;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.common.Channel;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.Tuple;
import de.schwarz.diceroller.commands.common.User;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import de.schwarz.diceroller.commands.stats.chart.ChartConfig;
import de.schwarz.diceroller.commands.stats.chart.DataSet;
import de.schwarz.diceroller.commands.stats.replyStrategy.GraphVisualization;
import de.schwarz.diceroller.commands.stats.replyStrategy.ReplyStrategy;
import de.schwarz.diceroller.commands.stats.replyStrategy.TextTable;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class GetStatsCommand implements TextCommandHandler {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		long channelId = event.getChannel().getIdLong();
		Channel channel = Stats.trackedChannels.stream().filter(c -> c.getChannelId() == channelId).findFirst().orElse(null);
		if (channel == null) return null;

		Optional<ChartConfig> chartConfig = createChartConfig(event.getMessage().getContentDisplay(), event.getAuthor(), channel);
		if (chartConfig.isEmpty())
			return new ReplyData("No information for this user in this channel!");

		List<Tuple<Integer, List<Tuple<Integer, Integer>>>> datasets = generateDataSets(chartConfig.get().users());

		ReplyStrategy strategy = getReplyStrategy(event.getMessage().getContentDisplay());
		return strategy.accept(event, chartConfig.get().title(), datasets);
	}

	protected ReplyStrategy getReplyStrategy(String contentDisplay) {
		if (contentDisplay.contains(" --table")) {
			return new TextTable();
		}
		return new GraphVisualization();
	}

	protected Optional<ChartConfig> createChartConfig(String msg, net.dv8tion.jda.api.entities.User author, Channel channel) {
		final String baseTitle = "Dice Roll Results Visualized";
		if (msg.contains(" --user")) {
			User user = channel.getUserList().stream().filter(trackedUser -> trackedUser.getUserId() == author.getIdLong()).findFirst().orElse(null);
			if (user == null) {
				return Optional.empty();
			}
			return Optional.of(new ChartConfig(baseTitle + " for " + user.getUserName(), new User[]{user}));
		} else {
			return Optional.of(new ChartConfig(baseTitle, channel.getUserList().toArray(new User[0])));
		}
	}

	protected List<Tuple<Integer, List<Tuple<Integer, Integer>>>> generateDataSets(User... users) {
		// Dataset erstellen für jeden Die den wir finden! also für jeden w20 oder w8 etc...
		HashMap<Integer, DataSet> dataSetMap = new HashMap<>();

		// alle ergebnisse zusammengetragen
		for (User user : users) {
			for (Map.Entry<Integer, List<Integer>> entry : user.getRolls().entrySet()) {
				Integer die = entry.getKey();
				List<Integer> results = entry.getValue();
				DataSet dataSet = dataSetMap.computeIfAbsent(die, (d) -> {
					DataSet set = new DataSet();
					set.setDie(d);
					set.setCountingMap(new HashMap<>());
					set.setValues(new ArrayList<>());
					return set;
				});

				dataSet.getValues().addAll(results);
			}
		}

		// gezählt welches ergebnis wie oft vorkommt
		dataSetMap.values()
				.forEach(dataset -> dataset.getValues()
						.forEach(value -> dataset.getCountingMap().compute(value, (key, currentCount) -> currentCount == null ? 1 : currentCount + 1)));

		dataSetMap.values()
				.forEach(dataset -> {
					dataset.getCountingMap().forEach((key, value) -> dataset.getResultList().add(new Tuple<>(key, value)));
					dataset.getResultList().sort(Comparator.comparing(Tuple::getOne));
				});

		return dataSetMap
				.entrySet()
				.stream()
				.map(entry -> new Tuple<>(entry.getKey(), entry.getValue().getResultList()))
				.sorted(Comparator.comparing(Tuple::getOne))
				.collect(Collectors.toList());
	}

}

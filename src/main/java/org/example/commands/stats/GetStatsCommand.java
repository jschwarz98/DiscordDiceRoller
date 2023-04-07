package org.example.commands.stats;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.TextCommandHandler;
import org.example.commands.common.Channel;
import org.example.commands.common.Stats;
import org.example.commands.common.Tuple;
import org.example.commands.common.User;
import org.example.commands.common.messages.AbstractMessageData;
import org.example.commands.stats.chart.ChartConfig;
import org.example.commands.stats.chart.DataSet;
import org.example.commands.stats.replyStrategy.GraphVisualization;
import org.example.commands.stats.replyStrategy.ReplyStrategy;
import org.example.commands.stats.replyStrategy.TextTable;

import java.util.*;
import java.util.stream.Collectors;

public class GetStatsCommand implements TextCommandHandler {

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		long channelId = event.getChannel().getIdLong();
		Channel channel = Stats.trackedChannels.stream().filter(c -> c.getChannelId() == channelId).findFirst().orElse(null);
		if (channel == null) return null;

		Optional<ChartConfig> chartConfig = createChartConfig(event, channel);
		if (chartConfig.isEmpty()) return null;

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

	protected Optional<ChartConfig> createChartConfig(MessageReceivedEvent event, Channel channel) {
		final String baseTitle = "Dice Roll Results Visualized";
		if (event.getMessage().getContentDisplay().contains(" --user")) {
			User user = channel.getUserList().stream().filter(trackedUser -> trackedUser.getUserId() == event.getAuthor().getIdLong()).findFirst().orElse(null);
			if (user == null) {
				event.getMessage().reply("No information for this user in this channel!").queue();
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

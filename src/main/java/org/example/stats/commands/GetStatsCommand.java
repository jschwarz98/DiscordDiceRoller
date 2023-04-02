package org.example.stats.commands;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.example.commands.TextCommandHandler;
import org.example.rolling.Tuple;
import org.example.stats.Channel;
import org.example.stats.JFXInitialization;
import org.example.stats.Stats;
import org.example.stats.User;
import org.example.stats.chart.ChartConfig;
import org.example.stats.chart.DataSet;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GetStatsCommand implements TextCommandHandler {

	private static final JFXInitialization JFX_INITIALIZATION = JFXInitialization.get();

	@Override
	public void accept(MessageReceivedEvent event) {
		long channelId = event.getChannel().getIdLong();
		Channel channel = Stats.trackedChannels.stream().filter(c -> c.getChannelId() == channelId).findFirst().orElse(null);
		if (channel == null) return;

		Optional<ChartConfig> chartConfig = createChartConfig(event, channel);
		if (chartConfig.isEmpty()) return;

		List<Tuple<Integer, List<Tuple<Integer, Integer>>>> datasets = generateDataSets(chartConfig.get().users());

		// Run the JavaFX code on the JavaFX application thread
		Platform.runLater(() -> {
			final BarChart<String, Number> barChart = createBarChart(chartConfig.get().title());
			WritableImage graph = drawGraphFromDataSets(datasets, barChart);
			byte[] outputImage = generateImage(graph);
			if (outputImage == null) {
				event.getMessage().reply("Error creating graph...").queue();
				return;
			}
			ByteArrayInputStream graphImageAsFile = new ByteArrayInputStream(outputImage);
			event.getMessage().reply("Here you go~").addFiles(FileUpload.fromData(graphImageAsFile, "stats.png")).queue();
		});

	}

	private Optional<ChartConfig> createChartConfig(MessageReceivedEvent event, Channel channel) {
		final String baseTitle = "Dice Roll Results Visualized";
		if (event.getMessage().getContentDisplay().startsWith("!stats --user")) {
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

	private WritableImage drawGraphFromDataSets(List<Tuple<Integer, List<Tuple<Integer, Integer>>>> datasets, BarChart<String, Number> barChart) {
		for (Tuple<Integer, List<Tuple<Integer, Integer>>> dataset : datasets) {
			barChart.getData().add(createDataSeries(dataset.getOne(), dataset.getTwo()));
		}
		JFX_INITIALIZATION.scene.setRoot(barChart);
		return barChart.snapshot(JFX_INITIALIZATION.parameters, null);
	}

	@NotNull
	private BarChart<String, Number> createBarChart(String title) {
		final CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Result");

		final NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Amount");

		final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle(title);

		return barChart;
	}

	private XYChart.Series<String, Number> createDataSeries(Integer die, List<Tuple<Integer, Integer>> results) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("W" + die);
		results.forEach(result -> series.getData().add(new XYChart.Data<>("" + result.getOne(), result.getTwo())));
		return series;
	}

	private byte[] generateImage(WritableImage image) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputStream);
			return outputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Tuple<Integer, List<Tuple<Integer, Integer>>>> generateDataSets(User... users) {
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

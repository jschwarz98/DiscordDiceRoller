package org.example.stats.commands;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
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
import org.example.stats.Stats;
import org.example.stats.User;
import org.example.stats.chart.DataSet;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GetStatsCommand implements TextCommandHandler {

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000 * 9 / 16;

	@Override
	public void accept(MessageReceivedEvent event) {
		long channelId = event.getChannel().getIdLong();
		Channel channel = Stats.trackedChannels.stream().filter(c -> c.getChannelId() == channelId).findFirst().orElse(null);
		if (channel == null) return;

		User[] users;
		final String baseTitle = "Dice Roll Results Visualized";
		final String title;
		if (event.getMessage().getContentDisplay().startsWith("!stats --user")) {
			User user = channel.getUserList().stream().filter(trackedUser -> trackedUser.getUserId() == event.getAuthor().getIdLong()).findFirst().orElse(null);
			if (user == null) {
				event.getMessage().reply("No information for this user in this channel!").queue();
				return;
			}
			users = new User[]{user};
			title = baseTitle + " for " + user.getUserName();
		} else {
			users = channel.getUserList().toArray(new User[0]);
			title = baseTitle;
		}
		// generate dataset for each die value
		List<Tuple<Integer, List<Tuple<Integer, Integer>>>> datasets = generateDataSets(users);
		// Construct the chart image
		// Initialize the JavaFX runtime
		JFXPanel fxPanel = new JFXPanel();
		// Run the JavaFX code on the JavaFX application thread
		Platform.runLater(() -> {
			final CategoryAxis xAxis = new CategoryAxis();
			final NumberAxis yAxis = new NumberAxis();
			final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
			bc.setTitle(title);
			xAxis.setLabel("Result");
			yAxis.setLabel("Amount");

			Scene scene = new Scene(bc, WIDTH, HEIGHT);

			for (Tuple<Integer, List<Tuple<Integer, Integer>>> dataset : datasets) {
				bc.getData().add(createDataSeries(dataset.getOne(), dataset.getTwo()));
			}

			// Take a snapshot of the bar chart and save it as an image
			SnapshotParameters parameters = new SnapshotParameters();
			parameters.setViewport(new Rectangle2D(0, 0, WIDTH, HEIGHT));
			WritableImage image = bc.snapshot(parameters, null);
			// Save the image to a file or do something else with it

			// Send the chart image as a regular message
			byte[] output = generateImage(image);
			if (output == null) {
				event.getMessage().reply("Error creating graph...").queue();
				return;
			}
			// Convert the ByteArrayOutputStream to an InputStream
			ByteArrayInputStream inputStream = new ByteArrayInputStream(output);
			event.getMessage().reply("Here you go~").addFiles(FileUpload.fromData(inputStream, "stats.png")).queue();
		});

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

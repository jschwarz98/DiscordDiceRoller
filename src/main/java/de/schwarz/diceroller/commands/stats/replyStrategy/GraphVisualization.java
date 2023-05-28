package de.schwarz.diceroller.commands.stats.replyStrategy;

import de.schwarz.diceroller.commands.common.AggregatedDiceResult;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import de.schwarz.diceroller.commands.stats.chart.JFXInitialization;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.util.StringConverter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GraphVisualization implements ReplyStrategy {

	private static final JFXInitialization JFX = JFXInitialization.get();

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event, String title, List<AggregatedDiceResult> datasets) {
		CompletableFuture<FileUpload> futureImage = new CompletableFuture<>();
		Platform.runLater(() -> {
			final BarChart<String, Number> barChart = createBarChart(title);
			WritableImage graph = drawGraphFromDataSets(datasets, barChart);
			byte[] outputImage = generateImage(graph);

			if (outputImage == null) {
				futureImage.completeExceptionally(new Exception("Error creating graph..."));
			} else {
				ByteArrayInputStream graphImageAsFile = new ByteArrayInputStream(outputImage);
				futureImage.complete(FileUpload.fromData(graphImageAsFile, "stats.png"));
			}
		});

		ReplyData reply;
		try {
			FileUpload fileUpload = futureImage.get();
			reply = new ReplyData("Here you go~");
			reply.setFiles(fileUpload);
		} catch (ExecutionException e) {
			reply = new ReplyData(e.getMessage());
		} catch (InterruptedException | CancellationException ignored) {
			reply = new ReplyData("Error running graph task!");
		}
		return reply;
	}

	@NotNull
	private BarChart<String, Number> createBarChart(String title) {
		final CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Result");

		final NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Amount");
		yAxis.setTickUnit(1);
		yAxis.setMinorTickVisible(false);
		yAxis.setTickLabelFormatter(new yAxisLabelConverter());

		final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle(title);

		return barChart;
	}

	private WritableImage drawGraphFromDataSets(List<AggregatedDiceResult> datasets, BarChart<String, Number> barChart) {
		for (AggregatedDiceResult dataset : datasets) {
			barChart.getData().add(createDataSeries(dataset));
		}
		JFX.scene.setRoot(barChart);
		return barChart.snapshot(JFX.parameters, null);
	}

	private XYChart.Series<String, Number> createDataSeries(AggregatedDiceResult dataset) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("W" + dataset.getDie().die());
		dataset.getAggregatedDiceResultList()
				.getMap()
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.forEachOrdered((entry) -> series.getData().add(new XYChart.Data<>("" + entry.getKey(), entry.getValue())));
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

	private static class yAxisLabelConverter extends StringConverter<Number> {
		@Override
		public String toString(Number object) {
			if (object.intValue() != object.doubleValue())
				return "";
			return "" + (object.intValue());
		}

		@Override
		public Number fromString(String string) {
			Number val = Double.parseDouble(string);
			return val.intValue();
		}
	}
}

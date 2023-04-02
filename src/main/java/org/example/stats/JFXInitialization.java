package org.example.stats;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

public class JFXInitialization {

	private static JFXInitialization instance = null;
	public final int WIDTH = 1000;
	public final int HEIGHT = 1000 * 9 / 16;
	public final SnapshotParameters parameters = new SnapshotParameters();
	public Scene scene;

	private JFXInitialization() {
		Platform.startup(() -> System.out.println("Starting JavaFX Thread..."));

		parameters.setViewport(new Rectangle2D(0, 0, WIDTH, HEIGHT));
		scene = new Scene(new BarChart<>(new CategoryAxis(), new NumberAxis()), WIDTH, HEIGHT);
	}

	public static JFXInitialization get() {
		if (instance == null)
			instance = new JFXInitialization();
		return instance;
	}
}

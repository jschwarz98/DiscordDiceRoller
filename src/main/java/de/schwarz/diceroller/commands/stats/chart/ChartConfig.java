package de.schwarz.diceroller.commands.stats.chart;

import de.schwarz.diceroller.commands.common.User;

public record ChartConfig(String title, User[] users) {
}

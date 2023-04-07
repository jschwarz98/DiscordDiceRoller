package org.example.commands.stats.chart;

import org.example.commands.common.User;

public record ChartConfig(String title, User[] users) {
}

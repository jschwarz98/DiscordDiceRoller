package de.schwarz.diceroller.commands.stats.statReplyStrategy;

import de.schwarz.diceroller.commands.common.AggregatedDiceResult;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;

import java.util.List;

@FunctionalInterface
public interface StatReplyStrategy {


	AbstractMessageData accept(String title, List<AggregatedDiceResult> datasets);

}

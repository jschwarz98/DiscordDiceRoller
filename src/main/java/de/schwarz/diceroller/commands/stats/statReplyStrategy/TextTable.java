package de.schwarz.diceroller.commands.stats.statReplyStrategy;

import de.schwarz.diceroller.commands.common.AggregatedDiceResult;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;

import java.util.List;

public class TextTable implements StatReplyStrategy {

	@Override
	public AbstractMessageData accept(String title, List<AggregatedDiceResult> datasets) {
		return new ReplyData("Not implemented yet...");
	}

}

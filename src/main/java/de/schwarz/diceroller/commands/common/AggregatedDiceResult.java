package de.schwarz.diceroller.commands.common;

public class AggregatedDiceResult {

	private final Dice die;
	private final AggregatedDiceResultList aggregatedDiceResultList;

	public AggregatedDiceResult(Dice die) {
		this.die = die;
		aggregatedDiceResultList = new AggregatedDiceResultList();
	}

	public AggregatedDiceResult(Dice die, AggregatedDiceResultList list) {
		this.die = die;
		aggregatedDiceResultList = list;
	}

	public Dice getDie() {
		return die;
	}

	public AggregatedDiceResultList getAggregatedDiceResultList() {
		return aggregatedDiceResultList;
	}
}

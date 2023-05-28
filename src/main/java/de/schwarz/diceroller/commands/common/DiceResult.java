package de.schwarz.diceroller.commands.common;

public class DiceResult {

	private final Dice die;
	private final DiceResultList resultList;

	public DiceResult(Dice die) {
		this.die = die;
		this.resultList = new DiceResultList();
	}

	public Dice getDie() {
		return die;
	}

	public DiceResultList getResultList() {
		return resultList;
	}


}

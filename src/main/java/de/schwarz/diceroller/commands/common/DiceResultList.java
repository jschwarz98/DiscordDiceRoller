package de.schwarz.diceroller.commands.common;

import java.util.ArrayList;
import java.util.List;

public class DiceResultList {

	private final List<Integer> results;

	public DiceResultList() {
		results = new ArrayList<>();
	}

	public List<Integer> getResults() {
		return results;
	}

	public void addResult(Integer result) {
		this.results.add(result);
	}

}

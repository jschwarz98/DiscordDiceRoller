package de.schwarz.diceroller.commands.common;

import java.util.HashMap;
import java.util.Map;

public class AggregatedDiceResultList {

	private final Map<Integer, Integer> map;

	public AggregatedDiceResultList() {
		map = new HashMap<>();
	}

	public AggregatedDiceResultList(Map<Integer, Integer> map) {
		this.map = map;
	}

	public Map<Integer, Integer> getMap() {
		return map;
	}
}

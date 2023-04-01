package org.example.stats.chart;

import org.example.rolling.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSet {

	private final List<Tuple<Integer, Integer>> resultList = new ArrayList<>();
	private int die;
	private List<Integer> values = new ArrayList<>();
	private HashMap<Integer, Integer> countingMap = new HashMap<>();

	public int getDie() {
		return die;
	}

	public void setDie(int die) {
		this.die = die;
	}

	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}

	public HashMap<Integer, Integer> getCountingMap() {
		return countingMap;
	}

	public void setCountingMap(HashMap<Integer, Integer> countingMap) {
		this.countingMap = countingMap;
	}

	public List<Tuple<Integer, Integer>> getResultList() {
		return resultList;
	}
}

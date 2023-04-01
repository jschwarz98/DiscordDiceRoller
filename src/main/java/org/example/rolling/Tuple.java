package org.example.rolling;

public class Tuple<K, V> {

	private K one;
	private V two;

	public Tuple(K one, V two) {
		this.one = one;
		this.two = two;
	}

	public K getOne() {
		return one;
	}

	public void setOne(K one) {
		this.one = one;
	}

	public V getTwo() {
		return two;
	}

	public void setTwo(V two) {
		this.two = two;
	}
}

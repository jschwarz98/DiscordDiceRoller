package de.schwarz.diceroller.commands.common;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Dice(Integer die) implements Comparable<Dice> {
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Dice dice = (Dice) o;
		return die.equals(dice.die);
	}

	@Override
	public int hashCode() {
		return Objects.hash(die);
	}

	@Override
	public int compareTo(@NotNull Dice o) {
		return this.die.compareTo(o.die);
	}
}

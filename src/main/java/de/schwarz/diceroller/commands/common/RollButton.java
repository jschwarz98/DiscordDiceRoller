package de.schwarz.diceroller.commands.common;

public enum RollButton {

	W4("w4", "W4"),
	W6("w6", "W6"),
	W8("w8", "W8"),
	W10("w10", "W10"),
	W12("w12", "W12"),
	W20("w20", "W20");

	public final String id;
	public final String label;

	RollButton(String id, String label) {
		this.id = id;
		this.label = label;
	}
}

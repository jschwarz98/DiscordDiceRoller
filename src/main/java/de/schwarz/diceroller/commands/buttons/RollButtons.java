package de.schwarz.diceroller.commands.buttons;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RollButtons {

	private static final List<Button> buttons = Arrays.stream(RollButton.values())
			.map(button -> Button.primary(button.id, button.label))
			.collect(Collectors.toList());

	public static ActionRow getRollButtons_4_6_8() {
		return ActionRow.of(buttons.subList(0, 3)); // 4, 6, 8
	}

	public static ActionRow getRollButtons_10_12_20() {
		return ActionRow.of(buttons.get(3),
				buttons.get(4),
				buttons.get(5).withStyle(ButtonStyle.DANGER)); // 10, 12, 20
	}

}

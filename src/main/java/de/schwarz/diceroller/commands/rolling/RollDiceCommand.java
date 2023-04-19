package de.schwarz.diceroller.commands.rolling;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.common.RollButton;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.Tuple;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RollDiceCommand implements TextCommandHandler {
	private static final String start = "!roll";
	private static final Random r = new Random();

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		String content = event.getMessage().getContentDisplay();
		if (!content.startsWith(start)) {
			return null;
		}

		String rest = content.substring(start.length()).trim();
		String[] args;

		if (rest.isEmpty()) {
			args = new String[]{"1w20"};
		} else {
			args = rest.split(" ");
		}

		return performRoll(event.getChannel(), event.getAuthor(), args);
	}

	public ReplyData performRoll(MessageChannelUnion channel, User user, String... args) {
		// 1. needed rolls
		List<Tuple<Integer, Integer>> neededRolls = getRolls(args);
		// 2. roll them
		List<Tuple<Integer, List<Integer>>> results = roll(neededRolls);
		// 3. track them if necessary
		Stats.track(channel, user, results);
		// 4. return result string
		String resultString = buildResultString(results);

		ReplyData replyData = new ReplyData(resultString);
		List<Button> buttons = Arrays.stream(RollButton.values())
				.map(button -> Button.primary(button.id, button.label))
				.collect(Collectors.toList());

		replyData.setActionRows(ActionRow.of(buttons.subList(0, 3)), ActionRow.of(buttons.subList(3, buttons.size())));

		return replyData;
	}

	private List<Tuple<Integer, List<Integer>>> roll(List<Tuple<Integer, Integer>> neededDiceRolls) {
		List<Tuple<Integer, List<Integer>>> result = new ArrayList<>();

		for (Tuple<Integer, Integer> roll : neededDiceRolls) {
			Integer die = roll.getOne();
			Integer amountOfDice = roll.getTwo();
			Tuple<Integer, List<Integer>> res = new Tuple<>(die, new ArrayList<>());

			for (int i = 0; i < amountOfDice; i++) {
				res.getTwo().add(r.nextInt(die) + 1);
			}

			result.add(res);
		}

		return result;
	}

	@NotNull
	private String buildResultString(List<Tuple<Integer, List<Integer>>> rollResults) {
		StringBuilder resultString = new StringBuilder();

		rollResults.forEach(roll -> {
			resultString.append("W")
					.append(roll.getOne())
					.append(": ");

			resultString.append(roll.getTwo().get(0));
			for (int i = 1; i < roll.getTwo().size(); i++) {
				resultString
						.append(", ")
						.append(roll.getTwo().get(i));

			}
			resultString.append(System.lineSeparator());
		});
		return resultString.isEmpty() ? "I don't understand your command. Please try again!" : resultString.toString().trim();
	}

	@NotNull
	private List<Tuple<Integer, Integer>> getRolls(String[] args) {
		List<Tuple<Integer, Integer>> neededRolls = new ArrayList<>();

		for (String arg : args) {
			String argLowerCase = arg.toLowerCase();
			if (!argLowerCase.contains("w")) continue;
			String firstPart = argLowerCase.split("w")[0];
			String secondPart = argLowerCase.split("w")[1];
			int amount = 1;
			int die;
			try {
				if (firstPart != null && !firstPart.isEmpty()) {
					amount = Integer.parseInt(firstPart);
					if (amount > 1000) continue;
				}
				die = Integer.parseInt(secondPart);
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				System.out.println(argLowerCase);
				continue;
			}
			Tuple<Integer, Integer> t = neededRolls.stream().filter(tuple -> tuple.getOne().equals(die)).findFirst().orElseGet(() -> {
				Tuple<Integer, Integer> newRoll = new Tuple<>(die, 0);
				neededRolls.add(newRoll);
				return newRoll;
			});
			t.setTwo(t.getTwo() + amount);
		}
		return neededRolls;
	}


}

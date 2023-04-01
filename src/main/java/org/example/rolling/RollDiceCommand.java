package org.example.rolling;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.commands.TextCommandHandler;
import org.example.stats.Stats;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RollDiceCommand implements TextCommandHandler {
	private static final String start = "!roll ";
	private static final Random r = new Random();

	@Override
	public void accept(MessageReceivedEvent event) {
		String content = event.getMessage().getContentDisplay();
		if (!content.startsWith(start)) {
			return;
		}

		String rest = content.substring(start.length());
		String[] args = rest.split(" ");

		// 1. needed rolls

		List<Tuple<Integer, Integer>> neededRolls = getRolls(args);
		// 2. roll them
		List<Tuple<Integer, List<Integer>>> results = roll(neededRolls);
		// 3. track them if necessary
		Stats.track(event.getChannel(), event.getAuthor(), results);

		// 4. return result string
		String resultString = buildResultString(results);

		event.getMessage().reply(resultString).queue();
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
		return resultString.isEmpty() ? "I don't understand your command. Please try again!" : resultString.toString();
	}

	@NotNull
	private List<Tuple<Integer, Integer>> getRolls(String[] args) {
		List<Tuple<Integer, Integer>> neededRolls = new ArrayList<>();

		for (String arg : args) {
			if (!arg.contains("w")) continue;
			String firstPart = arg.split("w")[0];
			String secondPart = arg.split("w")[1];
			int amount = 1;
			int die;
			try {
				if (firstPart != null && !firstPart.isEmpty()) {
					amount = Integer.parseInt(firstPart);
					if (amount > 1000) continue;
				}
				die = Integer.parseInt(secondPart);
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				System.out.println(arg);
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

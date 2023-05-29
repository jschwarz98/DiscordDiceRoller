package de.schwarz.diceroller.commands.rolling;

import de.schwarz.diceroller.commands.CommandHandler;
import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.buttons.RollButtons;
import de.schwarz.diceroller.commands.buttons.StatButtons;
import de.schwarz.diceroller.commands.common.Dice;
import de.schwarz.diceroller.commands.common.DiceResult;
import de.schwarz.diceroller.commands.common.Stats;
import de.schwarz.diceroller.commands.common.Tuple;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.commands.common.messages.ReplyData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RollDiceCommand implements TextCommandHandler {
	private static final Random r = new Random();

	@Override
	public AbstractMessageData accept(MessageReceivedEvent event) {
		String content = event.getMessage().getContentDisplay().trim();
		String rest = content.substring(CommandHandler.ROLL_DICE.getPrefix().length()).trim();
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
		List<DiceResult> results = roll(neededRolls);
		// 3. track them if necessary
		Stats.track(channel, user, results);
		// 4. return result string
		String resultString = buildResultString(results);

		ReplyData replyData = new ReplyData(resultString);

		replyData.setActionRows(
				RollButtons.getRollButtons_4_6_8(),
				RollButtons.getRollButtons_10_12_20(),
				StatButtons.getStatButtons(Stats.channelIsTracked(channel.getIdLong()))); // starttracking || stats, userstats, stoptracking


		return replyData;
	}

	private List<DiceResult> roll(List<Tuple<Integer, Integer>> neededDiceRolls) {
		List<DiceResult> result = new ArrayList<>();

		for (Tuple<Integer, Integer> roll : neededDiceRolls) {
			Integer die = roll.getOne();
			Integer amountOfDice = roll.getTwo();
			DiceResult res = new DiceResult(new Dice(die));

			for (int i = 0; i < amountOfDice; i++) {
				res.getResultList().getResults().add(r.nextInt(die) + 1);
			}

			result.add(res);
		}

		return result;
	}

	@NotNull
	private String buildResultString(List<DiceResult> rollResults) {
		StringBuilder resultString = new StringBuilder();

		rollResults.forEach(roll -> {
			resultString.append("W")
					.append(roll.getDie().die())
					.append(": ");

			resultString.append(roll.getResultList().getResults().get(0));
			for (int i = 1; i < roll.getResultList().getResults().size(); i++) {
				resultString
						.append(", ")
						.append(roll.getResultList().getResults().get(i));
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

package de.schwarz.diceroller.commands.rolling;

import de.schwarz.diceroller.commands.TextCommandHandler;
import de.schwarz.diceroller.commands.common.messages.AbstractMessageData;
import de.schwarz.diceroller.common.Defaults;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RollDiceCommandTest {

	private static TextCommandHandler handler;

	@BeforeAll
	public static void setUp() {
		handler = new RollDiceCommand();
	}

	@Test
	void accept_noParameters() {
		MessageReceivedEvent event = Defaults.createMockMessageEvent("!roll");
		AbstractMessageData reply = handler.accept(event);
		Assertions.assertNotNull(reply);
		Assertions.assertTrue(reply.getContent().startsWith("W20: "));

	}

	@Test
	void accept_oneParameters() {
		MessageReceivedEvent event = Defaults.createMockMessageEvent("!roll w10");
		AbstractMessageData reply = handler.accept(event);
		Assertions.assertNotNull(reply);
		Assertions.assertTrue(reply.getContent().startsWith("W10: "));
	}

	@Test
	void accept_oneParameters_false() {
		MessageReceivedEvent event = Defaults.createMockMessageEvent("!roll a10");
		AbstractMessageData reply = handler.accept(event);
		Assertions.assertNotNull(reply);
		Assertions.assertTrue(reply.getContent().startsWith("I don't understand your command."));
	}

	@Test
	void accept_twoParameters() {
		MessageReceivedEvent event = Defaults.createMockMessageEvent("!roll w10 w20");
		AbstractMessageData reply = handler.accept(event);
		Assertions.assertNotNull(reply);
		Assertions.assertTrue(reply.getContent().contains("W10: ") && reply.getContent().contains("W20: "));
	}

	@Test
	void accept_twoParameters_false() {
		MessageReceivedEvent event = Defaults.createMockMessageEvent("!roll a10 basdf51");
		AbstractMessageData reply = handler.accept(event);
		Assertions.assertNotNull(reply);
		Assertions.assertTrue(reply.getContent().startsWith("I don't understand your command."));
	}

	@Test
	void accept_twoParameters_partlyFalse() {
		MessageReceivedEvent event = Defaults.createMockMessageEvent("!roll w10 not valid information here");
		AbstractMessageData reply = handler.accept(event);
		Assertions.assertNotNull(reply);
		Assertions.assertTrue(reply.getContent().startsWith("W10: "));
	}

	@Test
	void accept_oneParameter_manyResults() {
		MessageReceivedEvent event = Defaults.createMockMessageEvent("!roll 3w4");
		AbstractMessageData reply = handler.accept(event);
		Assertions.assertNotNull(reply);
		System.out.println(reply.getContent());
		Assertions.assertTrue(reply.getContent().matches("W4: [1-4], [1-4], [1-4]"));
	}
}
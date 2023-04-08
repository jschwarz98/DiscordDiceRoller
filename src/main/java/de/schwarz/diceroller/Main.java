package de.schwarz.diceroller;

import de.schwarz.diceroller.commands.CommandHandlerBroker;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		String token;
		try {
			token = new BufferedReader(new FileReader("./.env"))
					.lines()
					.filter(line -> line.startsWith("TOKEN="))
					.map(line -> line.split("=")[1])
					.findFirst()
					.orElseThrow();
		} catch (FileNotFoundException e) {
			System.out.println("Needs a .env file with the token in it: TOKEN=...");
			throw new RuntimeException(e);
		}

		JDA jda = JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
		jda.addEventListener(new CommandHandlerBroker());
		jda.awaitReady();
		System.out.println("Bot is ready!");
	}


}
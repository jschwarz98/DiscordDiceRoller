package de.schwarz.diceroller.commands.common;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.mockito.Mockito;

public class Defaults {

	public static User createMockAuthor(String name, Long idLong) {
		User author = Mockito.mock(User.class);
		Mockito.when(author.getName()).thenReturn(name);
		Mockito.when(author.getIdLong()).thenReturn(idLong);
		return author;
	}

	/**
	 * @return Author name: testUser; id: 123
	 */
	public static User createMockAuthor() {
		return createMockAuthor("testUser", 123L);
	}

	public static MessageChannelUnion createMockChannel() {
		return createMockChannel(123L);
	}

	public static MessageChannelUnion createMockChannel(Long idLong) {
		MessageChannelUnion channel = Mockito.mock(MessageChannelUnion.class);
		Mockito.when(channel.getIdLong()).thenReturn(idLong);
		return channel;
	}

	public static MessageReceivedEvent createMockMessageEvent(String content, User mockAuthor, MessageChannelUnion mockChannel) {
		JDA jda = Mockito.mock(JDA.class);

		Message message = Mockito.mock(Message.class);
		Mockito.when(message.getAuthor()).thenReturn(mockAuthor);
		Mockito.when(message.getChannel()).thenReturn(mockChannel);
		Mockito.when(message.getContentDisplay()).thenReturn(content);

		MessageReceivedEvent event = new MessageReceivedEvent(jda, 1L, message);
		Mockito.when(event.getAuthor()).thenReturn(mockAuthor);

		return event;
	}

	public static MessageReceivedEvent createMockMessageEvent(String content) {
		return createMockMessageEvent(content, createMockAuthor(), createMockChannel());
	}

	public static MessageReceivedEvent createMockMessageEvent(String content, Long channelId) {
		return createMockMessageEvent(content, createMockAuthor(), createMockChannel(channelId));
	}

}

package de.schwarz.diceroller.commands.common.messages;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.FileUpload;

public abstract class AbstractMessageData {

	private MessageType type;
	private String content;
	private FileUpload[] files;
	private MessageEmbed[] embeds;

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public FileUpload[] getFiles() {
		return files;
	}

	public void setFiles(FileUpload... files) {
		this.files = files;
	}

	public MessageEmbed[] getEmbeds() {
		return embeds;
	}

	public void setEmbeds(MessageEmbed... embeds) {
		this.embeds = embeds;
	}
}

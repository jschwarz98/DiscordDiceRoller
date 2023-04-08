package de.schwarz.diceroller.commands.common.messages;

public class ReplyData extends AbstractMessageData {

	public ReplyData() {
		this.setType(MessageType.REPLY);
	}

	public ReplyData(String message) {
		this();
		this.setContent(message);
	}

}

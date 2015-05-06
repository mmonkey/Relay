package com.github.mmonkey.Relay.Utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public enum MessageRelayResult {

	SUCCESS (true, Texts.of(TextColors.GREEN, "Message(s) sent successfully!")),
	GATEWAY_USERNAME_PASSWORD_DECRYPTION_ERROR (false, Texts.of(TextColors.RED, "There is a problem decrypting gateway username or password.")),
	NO_MESSAGES_TO_SEND (false, Texts.of(TextColors.GOLD, "There are no messages to send.")),
	TRY_RELOADING_GATEWAYS (false, Texts.of(TextColors.RED, "There was a problem sending message(s), try reloading gateway(s)."));

	private final boolean sent;
	private final Text result;
	
	/**
	 * Check if alert message sent.
	 * @return boolean
	 */
	public boolean isSent() {
		return this.sent;
	}
	
	/**
	 * Result message
	 * @return Text
	 */
	public Text getResult() {
		return this.result;
	}
	
	MessageRelayResult(boolean sent, Text result) {
		this.sent = sent;
		this.result = result;
	}
	
}

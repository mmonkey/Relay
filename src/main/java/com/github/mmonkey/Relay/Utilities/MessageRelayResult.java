package com.github.mmonkey.Relay.Utilities;

public enum MessageRelayResult {

	SUCCESS (true, "Message(s) sent successfully!"),
	GATEWAY_USERNAME_PASSWORD_DECRYPTION_ERROR (false, "There is a problem decrypting gateway username or password."),
	METHOD_ALREADY_ACTIVATED (false, "You cannot send an actiavtion message to an already activated account."),
	NO_MESSAGES_TO_SEND (false, "There are no messages to send."),
	TRY_RELOADING_GATEWAYS (false, "There was a problem sending message(s), try reloading gateway(s).");

	private final boolean sent;
	private final String result;
	
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
	public String getResult() {
		return this.result;
	}
	
	MessageRelayResult(boolean sent, String result) {
		this.sent = sent;
		this.result = result;
	}
	
}

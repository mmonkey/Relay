package com.github.mmonkey.Relay.Services;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.MessageRelayResult;

public class SendActivationMessageRelayService extends MessageRelayService {

	public SendActivationMessageRelayService(Relay plugin) {
		super(plugin);
	}
	
	/**
	 * Send activation message to player.
	 * 
	 * @param recipient Player
	 * @param message String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendActivationMessage(Player recipient, String message) {

		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);

		return this.send(null, recipients, message, null, null, true);
	
	}
	
	/**
	 * Send activation message to player with separate text and email message templates.
	 * 
	 * @param recipient Player
	 * @param text String 
	 * @param email String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendActivationMessage(Player recipient, String text, String email) {

		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);

		return this.send(null, recipients, text, email, null, true);
	
	}
	
}

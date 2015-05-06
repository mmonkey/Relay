package com.github.mmonkey.Relay.Services;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Relay;

public class SendActivationMessageRelayService extends MessageRelayService {

	public SendActivationMessageRelayService(Relay plugin) {
		super(plugin);
	}
	
	/**
	 * Send activation message to player.
	 * 
	 * @param recipient Player
	 * @param message String
	 * @return
	 */
	public boolean sendActivationMessage(Player recipient, String message) {

		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);

		return this.send(null, recipients, message, null, true);
	
	}
	
	/**
	 * Send activation message to player with separate text and email message templates.
	 * 
	 * @param recipient
	 * @param text
	 * @param email
	 * @return
	 */
	public boolean sendActivationMessage(Player recipient, String text, String email) {

		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);

		return this.send(null, recipients, text, email, true);
	
	}
	
}

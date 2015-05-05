package com.github.mmonkey.Relay.Services;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Relay;

public class SendActivationMessageRelayService extends MessageRelayService {

	public SendActivationMessageRelayService(Relay plugin) {
		super(plugin);
	}
	
	public boolean sendActivationMessage(Player recipient, String message) {

		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);

		return this.send(null, recipients, message, true);
	
	}
	
}

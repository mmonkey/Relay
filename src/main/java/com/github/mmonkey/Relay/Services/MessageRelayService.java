package com.github.mmonkey.Relay.Services;

import com.github.mmonkey.Relay.Relay;

public class MessageRelayService implements RelayService {

	private Relay plugin;
	
	public MessageRelayService(Relay plugin) {
		this.plugin = plugin;
	}
}

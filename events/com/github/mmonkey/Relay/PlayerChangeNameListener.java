package com.github.mmonkey.Relay;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerUpdateEvent;

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.Relay;

public class PlayerChangeNameListener {

	private Relay plugin;
	
	@Subscribe
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		updateUsername(player);
		
	}
	
	@Subscribe
	public void onPlayerUpdate(PlayerUpdateEvent event) {
		
		Player player = event.getPlayer();
		updateUsername(player);
	
	}
	
	private void updateUsername(Player player) {
		
		List<String> contactIds = plugin.getContactStorageService().getContactList();
		
		if (contactIds.contains(player.getUniqueId().toString())) {
			
			Contact contact = plugin.getContactStorageService().getContact(player);
			
			if (!contact.getUsername().equals(player.getName()) && !contact.getUsername().equals("")) {
				contact.setUsername(player.getName());
				saveContact(contact, player);
			}
			
		}
		
	}
	
	private void saveContact(Contact contact, Player player) {
		plugin.getContactStorageService().saveContact(player, contact);
	}
	
	protected PlayerChangeNameListener(Relay plugin) {
		this.plugin = plugin;
	}
	
}

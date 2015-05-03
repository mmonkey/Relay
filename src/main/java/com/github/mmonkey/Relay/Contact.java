package com.github.mmonkey.Relay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;

public class Contact {
	
	private boolean acceptTerms = false;
	HashMap<ContactMethodTypes, String> methods = new HashMap<ContactMethodTypes, String>();	
	private List<UUID> blacklist = new ArrayList<UUID>();
	
	protected boolean acceptTerms() {
		return this.acceptTerms;
	}
	
	protected void acceptTerms(boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}
	
	protected HashMap<ContactMethodTypes, String> getMethods() {
		return this.methods;
	}
	
	protected List<UUID> getBlacklist() {
		return this.blacklist;
	}
	
	protected void addPlayerToBlacklist(Player player) {
		if (!this.blacklist.contains(player.getUniqueId())) {
			this.blacklist.add(player.getUniqueId());
		}
	}
	
	protected void removePlayerFromBlacklist(Player player) {
		if (this.blacklist.contains(player.getUniqueId())) {
			this.blacklist.remove(player.getUniqueId());
		}
	}
	
	protected Contact(ContactMethodTypes method, String contact) {
		this.methods.put(method, contact);
	}
	
}

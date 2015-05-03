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
	
	public boolean acceptTerms() {
		return this.acceptTerms;
	}
	
	public void acceptTerms(boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}
	
	public HashMap<ContactMethodTypes, String> getMethods() {
		return this.methods;
	}
	
	public void setMethods(HashMap<ContactMethodTypes, String> methods) {
		this.methods = methods;
	}
	
	public List<UUID> getBlacklist() {
		return this.blacklist;
	}
	
	public void setBlacklist(List<UUID> blacklist) {
		this.blacklist = blacklist;
	}
	
	public void addPlayerToBlacklist(Player player) {
		if (!this.blacklist.contains(player.getUniqueId())) {
			this.blacklist.add(player.getUniqueId());
		}
	}
	
	public void removePlayerFromBlacklist(Player player) {
		if (this.blacklist.contains(player.getUniqueId())) {
			this.blacklist.remove(player.getUniqueId());
		}
	}
	
	public Contact() {
	}
	
}

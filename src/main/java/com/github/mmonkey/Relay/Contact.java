package com.github.mmonkey.Relay;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

public class Contact {
	
	private String username = "";
	private boolean acceptTerms = false;
	List<ContactMethod> methods = new ArrayList<ContactMethod>();	
	private List<UUID> blacklist = new ArrayList<UUID>();
	
	protected String getUsername() {
		return this.username;
	}
	
	protected void setUsername(String username) {
		this.username = username;
	}
	
	protected boolean acceptTerms() {
		return this.acceptTerms;
	}
	
	protected void acceptTerms(boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}
	
	protected List<ContactMethod> getMethods() {
		return this.methods;
	}
	
	protected void setMethods(List<ContactMethod> methods) {
		this.methods = methods;
	}
	
	protected List<UUID> getBlacklist() {
		return this.blacklist;
	}
	
	protected void setBlacklist(List<UUID> blacklist) {
		this.blacklist = blacklist;
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
	
	protected Contact() {
	}
	
}

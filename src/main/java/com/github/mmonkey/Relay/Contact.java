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
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean acceptTerms() {
		return this.acceptTerms;
	}
	
	public void acceptTerms(boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}
	
	public List<ContactMethod> getMethods() {
		return this.methods;
	}
	
	public void setMethods(List<ContactMethod> methods) {
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

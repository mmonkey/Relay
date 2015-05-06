package com.github.mmonkey.Relay.Services;

import java.util.List;

import org.spongepowered.api.entity.player.Player;

public interface RelayService {

	/**
	 * Send message from server to player.
	 * 
	 * @param recipient Player
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(Player recipient, String message);
	
	/**
	 * Send message from server to multiple players.
	 * 
	 * @param recipients List<Player>
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(List<Player> recipients, String message);
	
	/**
	 * Send message from player to player.
	 * 
	 * @param sender Player
	 * @param recipient Player
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(Player sender, Player recipient, String message);
	
	/**
	 * Send message from player to multiple players.
	 * 
	 * @param sender Player
	 * @param recipients List<Player>
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(Player sender, List<Player> recipients, String message);
	
	/**
	 * Send message from server to player with separate text and email message templates.
	 * 
	 * @param recipient Player
	 * @param text String
	 * @param html String
	 * @return boolean
	 */
	public boolean sendMessage(Player recipient, String text, String html);
	
	/**
	 * Send message from server to multiple players with separate text and email message templates.
	 * 
	 * @param recipients List<Player>
	 * @param message String
	 * @param html String
	 * @return boolean
	 */
	public boolean sendMessage(List<Player> recipients, String text, String html);
	
	/**
	 * Send message from player to player with separate text and email message templates.
	 * 
	 * @param sender Player
	 * @param recipient Player
	 * @param text String
	 * @param html String
	 * @return boolean
	 */
	public boolean sendMessage(Player sender, Player recipient, String text, String html);
	
	/**
	 * Send message from player to multiple players with separate text and email message templates.
	 * 
	 * @param sender Player
	 * @param recipients List<Player>
	 * @param text String
	 * @param html String
	 * @return boolean
	 */
	public boolean sendMessage(Player sender, List<Player> recipients, String text, String html);
	
}

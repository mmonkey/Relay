package com.github.mmonkey.Relay;

import java.util.Collection;

public interface RelayService<T> {

	/**
	 * Send message from server to player.
	 * 
	 * @param recipient <T> (Player or String)
	 * @param message String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(T recipient, String message);
	
	/**
	 * Send message from server to multiple players.
	 * 
	 * @param recipients Collection<T> (Player or String)
	 * @param message String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(Collection<T> recipients, String message);
	
	/**
	 * Send message from player to player.
	 * 
	 * @param sender <T> (Player or String)
	 * @param recipient <T> (Player or String)
	 * @param message String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(T sender, T recipient, String message);
	
	/**
	 * Send message from player to multiple players.
	 * 
	 * @param sender <T> (Player or String)
	 * @param recipients Collection<T> (Player or String)
	 * @param message String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(T sender, Collection<T> recipients, String message);
	
	/**
	 * Send message from server to player with separate text and email message templates.
	 * 
	 * @param recipient <T> (Player or String)
	 * @param text String
	 * @param html String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(T recipient, String text, String html);
	
	/**
	 * Send message from server to multiple players with separate text and email message templates.
	 * 
	 * @param recipients Collection<T> (Player or String)
	 * @param message String
	 * @param html String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(Collection<T> recipients, String text, String html);
	
	/**
	 * Send message from player to player with separate text and email message templates.
	 * 
	 * @param sender <T> (Player or String)
	 * @param recipient <T> (Player or String)
	 * @param text String
	 * @param html String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(T sender, T recipient, String text, String html);
	
	/**
	 * Send message from player to multiple players with separate text and email message templates.
	 * 
	 * @param sender <T> (Player or String)
	 * @param recipients List<T> (Player or String)
	 * @param text String
	 * @param html String
	 * @return MessageRelayResult
	 */
	public MessageRelayResult sendMessage(T sender, Collection<T> recipients, String text, String html);
	
}

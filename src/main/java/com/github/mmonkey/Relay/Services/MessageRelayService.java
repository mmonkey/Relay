package com.github.mmonkey.Relay.Services;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Carriers;
import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Gateway;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.StorageUtil;

public class MessageRelayService implements RelayService {

	private Relay plugin;
	
	/**
	 * Send message from the server to a player
	 * 
	 * @param recipient Player
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(Player recipient, String message) {
		
		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);
		
		return send(null, recipients, message);
		
	}
	
	/**
	 * Send message from the server to multiple players
	 * 
	 * @param recipients List<Player>
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(List<Player> recipients, String message) {
		
		return send(null, recipients, message);
		
	}
	
	/**
	 * Send message from a player to a player
	 * 
	 * @param sender Player
	 * @param recipient Player
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(Player sender, Player recipient, String message) {
		
		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);
		
		return send(sender, recipients, message);
		
	}
	
	/**
	 * Send message from a player to multiple players
	 * 
	 * @param sender Player
	 * @param recipients List<Player>
	 * @param message String
	 * @return boolean
	 */
	public boolean sendMessage(Player sender, List<Player> recipients, String message) {
		
		return send(sender, recipients, message);
		
	}
	
	private boolean send(Player sender, List<Player> recipients, String message) {
		
		Gateway gateway = getGateway();
		
		if (gateway == null) {
			return false;
		}
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		
		if (gateway.sslEnabled()) {
			
			properties.put("mail.smtp.socketFactory.port", gateway.getPort());
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			
		} else {
			
			properties.put("mail.smtp.starttls.enable", "true");
			
		}
		
		properties.put("mail.smtp.host", gateway.getHost());
		properties.put("mail.smtp.port", gateway.getPort());
		
		String encryptionKey = plugin.getDefaultConfigService().getConfig()
				.getNode(StorageUtil.CONFIG_NODE_SETTINGS, StorageUtil.CONFIG_NODE_SECRET_KEY).getString();
		
		EncryptionUtil encryptionUtil = new EncryptionUtil(encryptionKey);
		
		Session session;
		final String username;
		final String password;
		
		try {
			
			username = encryptionUtil.decrypt(gateway.getUsername());
			password = encryptionUtil.decrypt(gateway.getPassword());
			
			session = Session.getInstance(properties,
				new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				}
			);
			
		} catch (GeneralSecurityException e) {
			return false;
		}
		
		List<Message> messages = getMessages(sender, recipients, session, message);
		
		if (messages == null) {
			return false;
		}
		
		try {
			
			Transport transport = session.getTransport("smtp");
			transport.connect(gateway.getHost(), gateway.getPort(), username, password);
		
			for (Message m: messages) {
				transport.sendMessage(m, m.getAllRecipients());
			}
		
		} catch (MessagingException e) {
			return false;
		}
		
		return true;
		
	}
	
	private List<Message> getMessages(Player sender, List<Player> recipients, Session session, String message) {
		
		List<Message> messages = new ArrayList<Message>();
		
		for (Player player: recipients) {
			
			try {
				
				String encryptionKey = plugin.getDefaultConfigService().getConfig()
					.getNode(StorageUtil.CONFIG_NODE_SETTINGS, StorageUtil.CONFIG_NODE_SECRET_KEY).getString();
				
				String emailSubject = plugin.getDefaultConfigService().getConfig()
					.getNode(StorageUtil.CONFIG_NODE_MESSAGES, StorageUtil.CONFIG_NODE_EMAIL_SUBJECT).getString();
				
				String smsSubject = plugin.getDefaultConfigService().getConfig()
					.getNode(StorageUtil.CONFIG_NODE_MESSAGES, StorageUtil.CONFIG_NODE_SMS_SUBJECT).getString();
				
				String displayName = plugin.getDefaultConfigService().getConfig()
					.getNode(StorageUtil.CONFIG_NODE_MESSAGES, StorageUtil.CONFIG_NODE_EMAIL_DISPLAY_NAME).getString();
				
				EncryptionUtil encryptionUtil = new EncryptionUtil(encryptionKey);
				Contact contact = plugin.getContactStorageService().getContact(player);
				List<ContactMethod> methods = contact.getMethods();
				
				for (ContactMethod method: methods) {
					
					Message emailMessage = new MimeMessage(session);
					List<String> addresses = new ArrayList<String>();
					Gateway gateway = getGateway();
					
					String fromEmailAddress = encryptionUtil.decrypt(gateway.getEmailAddress());
					String fromEmailUsername = encryptionUtil.decrypt(gateway.getUsername());
					String fromAddress = (!fromEmailAddress.equals("")) ? fromEmailAddress : fromEmailUsername;
					
					if (sender == null) {
						
						emailMessage.setFrom(new InternetAddress(fromAddress, displayName));
					
					} else {
						
						emailMessage.setFrom(new InternetAddress(fromAddress, sender.getName()));
						
					}
					
					for (int i = 0; i < method.getCarrier().getAddresses().length; i++) {	
						String address = encryptionUtil.decrypt(method.getAddress()) + method.getCarrier().getAddresses()[i];
						addresses.add(address);
					}
					
					for (String address: addresses) {
						emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
					}
					
					if (method.getCarrier().name().equals(Carriers.NO_CARRIER)) {
						
						emailMessage.setSubject(emailSubject);
					
					} else {
						
						emailMessage.setSubject(smsSubject);
						
					}
					
					emailMessage.setText(message);
					messages.add(emailMessage);
					
				}
				
			} catch (MessagingException e) {
				return null;
			} catch (UnsupportedEncodingException e) {
				return null;
			} catch (GeneralSecurityException e) {
				return null;
			}
		}
		
		return messages;
		
	}
	
	private Gateway getGateway() {
		
		List<String> gateways = plugin.getGatewayStorageService().getGatewayList();
		
		for (String name: gateways) {
			if (name.equals("Mandrill")) {
				return plugin.getGatewayStorageService().getGateway(name);
			}
		}
		
		if (gateways.size() > 0) {
		
			return plugin.getGatewayStorageService().getGateway(gateways.get(1));
		
		} else {
			
			return null;
			
		}
		
	}
	
	public MessageRelayService(Relay plugin) {
		this.plugin = plugin;
	}
}

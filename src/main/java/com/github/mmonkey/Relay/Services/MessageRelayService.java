package com.github.mmonkey.Relay.Services;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Gateway;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.MessageRelayResult;

public class MessageRelayService implements RelayService {

	protected Relay plugin;
	
	/**
	 * Send message from the server to a player.
	 * 
	 * @param recipient Player
	 * @param message String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(Player recipient, String message) {
		
		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);
		
		return send(null, recipients, message, null);
		
	}
	
	/**
	 * Send message from the server to multiple players.
	 * 
	 * @param recipients List<Player>
	 * @param message String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(List<Player> recipients, String message) {
		
		return send(null, recipients, message, null);
		
	}
	
	/**
	 * Send message from a player to a player.
	 * 
	 * @param sender Player
	 * @param recipient Player
	 * @param message String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(Player sender, Player recipient, String message) {
		
		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);
		
		return send(sender, recipients, message, null);
		
	}
	
	/**
	 * Send message from a player to multiple players.
	 * 
	 * @param sender Player
	 * @param recipients List<Player>
	 * @param message String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(Player sender, List<Player> recipients, String message) {
		
		return send(sender, recipients, message, null);
		
	}
	
	/**
	 * Send message from the server to a player with separate text and email message templates.
	 * 
	 * @param recipient Player
	 * @param text String
	 * @param html String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(Player recipient, String text, String html) {
		
		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);
		
		return send(null, recipients, text, html);
		
	}
	
	/**
	 * Send message from the server to multiple players with separate text and email message templates.
	 * 
	 * @param recipients List<Player>
	 * @param text String
	 * @param html String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(List<Player> recipients, String text, String html) {
		
		return send(null, recipients, text, html);
		
	}
	
	/**
	 * Send message from a player to a player with separate text and email message templates.
	 * 
	 * @param sender Player
	 * @param recipient Player
	 * @param text String
	 * @param html String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(Player sender, Player recipient, String text, String html) {
		
		List<Player> recipients = new ArrayList<Player>();
		recipients.add(recipient);
		
		return send(sender, recipients, text, html);
		
	}
	
	/**
	 * Send message from a player to multiple players with separate text and email message templates.
	 * 
	 * @param sender Player
	 * @param recipients List<Player>
	 * @param text String
	 * @param html String
	 * @return MessageRelayResult
	 */
	@Override
	public MessageRelayResult sendMessage(Player sender, List<Player> recipients, String text, String html) {
		
		return send(sender, recipients, text, html);
		
	}
	
	private MessageRelayResult send(Player sender, List<Player> recipients, String text, String html) {
		
		Gateway gateway = getGateway();
		
		if (gateway == null) {
			return MessageRelayResult.TRY_RELOADING_GATEWAYS;
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
		
		String secretKey = plugin.getDefaultConfigService().getConfig()
				.getNode(DefaultConfigStorageService.SETTINGS, DefaultConfigStorageService.SECRET_KEY).getString();
		
		EncryptionUtil encryptionUtil = new EncryptionUtil(secretKey);
		
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
			
			return MessageRelayResult.GATEWAY_USERNAME_PASSWORD_DECRYPTION_ERROR;
			
		} catch (UnsupportedEncodingException e) {
			
			return MessageRelayResult.GATEWAY_USERNAME_PASSWORD_DECRYPTION_ERROR;
			
		}
		
		List<Message> messages = getMessages(sender, recipients, session, gateway, text, html);
		
		if (messages == null) {
			return MessageRelayResult.NO_MESSAGES_TO_SEND;
		}
		
		try {
			
			Transport transport = session.getTransport("smtp");
			transport.connect(gateway.getHost(), gateway.getPort(), username, password);
		
			for (Message message: messages) {
				transport.sendMessage(message, message.getAllRecipients());
			}
		
		} catch (MessagingException e) {
			
			e.printStackTrace();
			return MessageRelayResult.TRY_RELOADING_GATEWAYS;
			
		}
		
		return MessageRelayResult.SUCCESS;
		
	}
	
	private List<Message> getMessages(Player sender, List<Player> recipients, Session session, Gateway gateway, String text, String html) {
		
		List<Message> messages = new ArrayList<Message>();
		
		for (Player player: recipients) {
			
			try {
				
				String secretKey = plugin.getDefaultConfigService().getConfig()
					.getNode(DefaultConfigStorageService.SETTINGS, DefaultConfigStorageService.SECRET_KEY).getString();
				
				String emailSubject = plugin.getDefaultConfigService().getConfig()
					.getNode(DefaultConfigStorageService.MESSAGES, DefaultConfigStorageService.EMAIL_SUBJECT).getString();
				
				String smsSubject = plugin.getDefaultConfigService().getConfig()
					.getNode(DefaultConfigStorageService.MESSAGES,DefaultConfigStorageService.SMS_SUBJECT).getString();
				
				String displayName = plugin.getDefaultConfigService().getConfig()
					.getNode(DefaultConfigStorageService.MESSAGES, DefaultConfigStorageService.EMAIL_DISPLAY_NAME).getString();
				
				EncryptionUtil encryptionUtil = new EncryptionUtil(secretKey);
				Contact contact = plugin.getContactStorageService().getContact(player);
				List<ContactMethod> methods = contact.getMethods();
				
				for (ContactMethod method: methods) {
					
					MimeMessage message = new MimeMessage(session);
					List<String> addresses = new ArrayList<String>();
					
					String fromEmailAddress = encryptionUtil.decrypt(gateway.getEmailAddress());
					String fromEmailUsername = encryptionUtil.decrypt(gateway.getUsername());
					String fromAddress = (!fromEmailAddress.equals("")) ? fromEmailAddress : fromEmailUsername;
					
					if (sender == null) {
						message.setFrom(new InternetAddress(fromAddress, displayName));
					} else {
						message.setFrom(new InternetAddress(fromAddress, sender.getName()));
					}
					
					for (int i = 0; i < method.getCarrier().getAddresses().length; i++) {	
						String address = encryptionUtil.decrypt(method.getAddress()) + method.getCarrier().getAddresses()[i];
						addresses.add(address);
					}
					
					for (String address: addresses) {
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
					}
					
					
					if (method.getType().equals(ContactMethodTypes.EMAIL)) {
						
						message.setSubject(emailSubject);
						message.setSentDate(new Date());
						
						if (html != null) {
							
							MimeMultipart multipart = new MimeMultipart();
							MimeBodyPart textPart = new MimeBodyPart();
							MimeBodyPart htmlPart = new MimeBodyPart();

							htmlPart.setContent(html, "text/html");
							textPart.setContent(text, "text/plain");
							
							multipart.addBodyPart(htmlPart);
							multipart.addBodyPart(textPart);
							
							message.setContent(multipart);

						} else {
							
							message.setText(text);
							
						}
						
					}
					
					if (method.getType().equals(ContactMethodTypes.SMS)) {
						
						message.setSubject(smsSubject);
						message.setText(text);
						
					}
					
					if (contact.acceptTerms() && method.isActivated()) {
						messages.add(message);
					}
					
				}
				
			} catch (MessagingException e) {
				e.printStackTrace();
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return messages;
		
	}
	
	protected Gateway getGateway() {
		
		List<String> gateways = plugin.getGatewayStorageService().getGatewayList();
		
		for (String name: gateways) {
			if (name.equals("Mandrill")) {
				return plugin.getGatewayStorageService().getGateway(name);
			}
		}
		
		if (gateways.size() > 0) {
		
			return plugin.getGatewayStorageService().getGateway(gateways.get(0));
		
		}
		
		return null;
		
	}
	
	public MessageRelayService(Relay plugin) {
		this.plugin = plugin;
	}
}

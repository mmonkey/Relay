package com.github.mmonkey.Relay;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Email.EmailMessage;

public class RelaySendSubcommand extends RelayCommand {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		boolean all = (args.hasAny("a")) ? (Boolean) args.getOne("a").get() : false;
		String message = (args.hasAny("message")) ? ((String) args.getOne("message").get()) : "";
		
		@SuppressWarnings("unchecked")
		Collection<String> players = (Collection<String>) ((args.hasAny("player")) ? args.getAll("player") : new ArrayList<String>());
		@SuppressWarnings("unchecked")
		Collection<String> templates = (Collection<String>) ((args.hasAny("template")) ? args.getAll("template") : new ArrayList<String>());
		
		Collection<UUID> allContacts = getAllContacts();
		
		String template = getTemplate(templates);
		EmailMessage email = new EmailMessage();
		
		MessageRelayResult result;
		
		//TODO add email template stuff
		
		if (!all) {
		
			MessageRelayService<String> service = new MessageRelayService<String>(plugin);
			
			if (src instanceof Player) {
				
				result = service.sendMessage(src.getName(), players, message);
			
			} else {
				
				result = service.sendMessage(players, message);
				
			}
		
		} else {
			
			MessageRelayService<UUID> service = new MessageRelayService<UUID>(plugin);
			
			if (src instanceof Player) {
				
				result = service.sendMessage(((Player) src).getUniqueId(), allContacts, message);
			
			} else {
				
				result = service.sendMessage(allContacts, message);
				
			}
			
		}
		
		if (result.isSent()) {
			
			src.sendMessage(Texts.of(TextColors.GREEN, "Message(s) succesfully sent!").builder().build());
			
		} else {
			
			if (src instanceof Player) {
				Relay.getLogger().info("Error sending message: Player " + src.getName() + " Error: " + result.getResult());
			}
			
			src.sendMessage(Texts.of(TextColors.GOLD + "Error sending message. Details: ", TextColors.RED, result.getResult()).builder().build());
			
			
		}
		return CommandResult.success();
		
	}
	
	private String getTemplate(Collection<String> templates) {
		
		if (!templates.isEmpty()) {
		
			for (String template: templates) {
			
				String filename = template.split("\\.")[0];
				String templateName = filename + ".mustache";
				
				File file = new File(plugin.getTemplateDir(), templateName);
				
				if (file.isFile()) {
					return templateName;
				}
				
			}
		
		}
			
		return plugin.getDefaultTemplate();
		
	}
	
	private Collection<UUID> getAllContacts() {
		
		Collection<String> contactIds = plugin.getContactStorageService().getContactList();
		Collection<UUID> contacts = new ArrayList<UUID>();
		
		for (String id: contactIds) {
			contacts.add(UUID.fromString(id));
		}
		
		return contacts;
		
	}
	
	protected RelaySendSubcommand(Relay plugin) {
		super(plugin);
	}

}

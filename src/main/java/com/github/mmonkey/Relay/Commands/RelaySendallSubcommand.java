package com.github.mmonkey.Relay.Commands;

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
import com.github.mmonkey.Relay.Services.HTMLTemplatingService;
import com.github.mmonkey.Relay.Services.MessageRelayResult;
import com.github.mmonkey.Relay.Services.MessageRelayService;

public class RelaySendallSubcommand extends RelayCommand {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		String message = (args.hasAny("message")) ? ((String) args.getOne("message").get()) : "";
		
		@SuppressWarnings("unchecked")
		Collection<String> templates = (Collection<String>) ((args.hasAny("template")) ? args.getAll("template") : new ArrayList<String>());
		
		Collection<UUID> allContacts = getAllContacts();
		
		HTMLTemplatingService templateService = new HTMLTemplatingService();
		templateService.setTemplateDirectory(plugin.getTemplateDir());
		
		EmailMessage email = getSendEmail(src, message);
		String emailMessage = templateService.parse(getTemplate(templates), email);
		
		MessageRelayService<UUID> service = new MessageRelayService<UUID>(plugin);
		MessageRelayResult result;
		
		if (src instanceof Player) {
			
			result = service.sendMessage(((Player) src).getUniqueId(), allContacts, message, emailMessage);
		
		} else {
			
			result = service.sendMessage(allContacts, message, emailMessage);
			
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
	
	private Collection<UUID> getAllContacts() {
		
		Collection<String> contactIds = plugin.getContactStorageService().getContactList();
		Collection<UUID> contacts = new ArrayList<UUID>();
		
		for (String id: contactIds) {
			contacts.add(UUID.fromString(id));
		}
		
		return contacts;
		
	}
	
	public RelaySendallSubcommand(Relay plugin) {
		super(plugin);
	}

}

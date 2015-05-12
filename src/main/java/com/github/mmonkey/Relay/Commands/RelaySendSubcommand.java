package com.github.mmonkey.Relay.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Services.MessageRelayService;

public class RelaySendSubcommand extends RelayCommand {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		Player player = (Player) src;
		
		boolean all = (args.hasAny("a")) ? (Boolean) args.getOne("a").get() : false;
		String message = (args.hasAny("message")) ? ((String) args.getOne("message").get()) : "";
		
		@SuppressWarnings("unchecked")
		Collection<String> players = (Collection<String>) ((args.hasAny("player")) ? args.getAll("player") : new ArrayList<String>());
		@SuppressWarnings("unchecked")
		Collection<String> templates = (Collection<String>) ((args.hasAny("template")) ? args.getAll("template") : new ArrayList<String>());
	
		Collection<Player> recipients = (all) ? getAllContactPlayers() : new ArrayList<Player>();
		
		if (!players.isEmpty() && !all) {
		
			// TODO get uuid from contact storage
			for(String name: players) {
				recipients.add((Player) this.plugin.getGame().getServer().getPlayer(name));
			}
		
		}
		
		String template = getTemplate(templates);
		
		MessageRelayService service = new MessageRelayService(plugin);
		
		//TODO add email message with template
		service.sendMessage(player, recipients, message);
		
		
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
	
	private List<Player> getAllContactPlayers() {
		
		List<Player> players = new ArrayList<Player>();
		List<String> contacts = plugin.getContactStorageService().getContactList();
		
		for (String contact: contacts) {
			
			players.add((Player) plugin.getGame().getServer().getPlayer(contact));
			
		}
		
		return players;
		
	}
	
	public RelaySendSubcommand(Relay plugin) {
		super(plugin);
	}

}

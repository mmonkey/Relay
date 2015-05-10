package com.github.mmonkey.Relay.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.FormatUtil;

public class UnregisterCommand implements CommandExecutor {

	private Relay plugin;
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean confirm = (args.hasAny("confirm")) ? (Boolean) args.getOne("confirm").get() : false;
		boolean cancel = (args.hasAny("cancel")) ? (Boolean) args.getOne("cancel").get() : false;
		String method = (args.hasAny("method")) ? ((String) args.getOne("method").get()) : "";
		
		Player player = (Player) src;
		
		if (cancel && !method.equals("")) {
			// TODO method wasn't deleted
			
			return CommandResult.success();
			
		}
		
		if (cancel && method.equals("")) {
			// TODO account wasn't deleted
			
			return CommandResult.success();
			
		}
		
		if (confirm && !method.equals("")) {
			
			boolean methodFound = false;
			List<String> list = plugin.getContactStorageService().getContactMethodList(player);
			
			for (String item: list) {
				
				if (item.equalsIgnoreCase(method)) {
					methodFound = true;
					plugin.getContactStorageService().deleteContactMethod(player, method.toUpperCase());
				}
				
			}
			
			if (methodFound) {
				
				player.sendMessage(
					FormatUtil.empty(),
					Texts.of(TextColors.GREEN, "Contact method ", TextColors.GOLD, method, TextColors.GREEN, " has been deleted.").builder().build()
				);
				
			} else {
				
				player.sendMessage(
					FormatUtil.empty(),
					Texts.of(TextColors.GOLD, "Contact method ", TextColors.RED, method, TextColors.GOLD, " was not found.").builder().build()
				);
				
			}
			
			return CommandResult.success();
			
		}
		
		if (confirm && method.equals("")) {
			
			plugin.getContactStorageService().deleteContact(player);
			
			player.sendMessage(
				FormatUtil.empty(),
				Texts.of(TextColors.GREEN, "All contact information has been deleted.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (!confirm && !cancel && !method.equals("")) {
			// TODO confirm delete method
			
			return CommandResult.success();
			
		}
		
		if (!confirm && !cancel && method.equals("")) {
			// TODO confirm delete account
			
			return CommandResult.success();
			
		}
		
		return CommandResult.empty();
		
	}
	
	public UnregisterCommand(Relay plugin) {
		this.plugin = plugin;
	}

}

package com.github.mmonkey.Relay.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandMessageFormatting;
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
		
		boolean delete = (args.hasAny("d")) ? (Boolean) args.getOne("d").get() : false;
		boolean cancel = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
		String method = (args.hasAny("method")) ? ((String) args.getOne("method").get()) : "";
		
		Player player = (Player) src;
		
		if (cancel && !method.equals("")) {
			
			player.sendMessage(
				CommandMessageFormatting.NEWLINE_TEXT,
				Texts.of(TextColors.GREEN, "Contact method ", TextColors.GOLD, method, TextColors.GREEN, " was not deleted.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (cancel && method.equals("")) {
			
			player.sendMessage(
				CommandMessageFormatting.NEWLINE_TEXT,
				Texts.of(TextColors.GREEN, "You account was not deleted.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (delete && !method.equals("")) {
			
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
		
		if (delete && method.equals("")) {
			
			plugin.getContactStorageService().deleteContact(player);
			
			player.sendMessage(
				FormatUtil.empty(),
				Texts.of(TextColors.GREEN, "All contact information has been deleted.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (!delete && !cancel && !method.equals("")) {
			
			TextBuilder message = Texts.builder();
			
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(Texts.of(TextColors.WHITE, "Would you like to delte method ", TextColors.GOLD, method, TextColors.WHITE, "?"));
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(getConfirmDeleteContactMethodAction(method), Texts.of(" "), getCancelDeleteContactMethodAction(method));
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			
			player.sendMessage(message.build());
			
			return CommandResult.success();
			
		}
		
		if (!delete && !cancel && method.equals("")) {
			
			TextBuilder message = Texts.builder();
			
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(Texts.of(TextColors.WHITE, "Would you like to your account?"));
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(getConfirmDeleteAccountAction(), Texts.of(" "), getCancelDeleteAccountAction());
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			
			player.sendMessage(message.build());
			
			return CommandResult.success();
			
		}
		
		return CommandResult.empty();
		
	}
	
	private Text getConfirmDeleteContactMethodAction(String method) {
		
		return Texts.builder("Yes, delete method " + method + ".")
			.onClick(TextActions.runCommand("/unregister -d " + method))
			.color(TextColors.GREEN)
			.style(TextStyles.UNDERLINE)
			.build();
		
	}
	
	private Text getCancelDeleteContactMethodAction(String method) {
		
		return Texts.builder("No, keep method " + method + ".")
			.onClick(TextActions.runCommand("/unregister -c " + method))
			.color(TextColors.RED)
			.style(TextStyles.UNDERLINE)
			.build();
		
	}
	
	private Text getConfirmDeleteAccountAction() {
		
		return Texts.builder("Yes, delete my account.")
			.onClick(TextActions.runCommand("/unregister -d"))
			.color(TextColors.GREEN)
			.style(TextStyles.UNDERLINE)
			.build();
		
	}
	
	private Text getCancelDeleteAccountAction() {
		
		return Texts.builder("No, keep my account.")
			.onClick(TextActions.runCommand("/unregister -c"))
			.color(TextColors.RED)
			.style(TextStyles.UNDERLINE)
			.build();
		
	}
	
	public UnregisterCommand(Relay plugin) {
		this.plugin = plugin;
	}

}

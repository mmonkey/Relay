package com.github.mmonkey.Relay.Commands;

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

import com.github.mmonkey.Relay.ContactMethod;
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
		String contactMethod = (args.hasAny("contactMethod")) ? ((String) args.getOne("contactMethod").get()) : "";
		
		Player player = (Player) src;
		
		if (cancel && !contactMethod.equals("")) {
			
			player.sendMessage(
				CommandMessageFormatting.NEWLINE_TEXT,
				Texts.of(TextColors.GREEN, "Contact method ", TextColors.GOLD, contactMethod, TextColors.GREEN, " was not deleted.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (cancel && contactMethod.equals("")) {
			
			player.sendMessage(
				CommandMessageFormatting.NEWLINE_TEXT,
				Texts.of(TextColors.GREEN, "You account was not deleted.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (delete && !contactMethod.equals("")) {
			
			ContactMethod method = plugin.getContactStorageService().getContactMethod(player, contactMethod);
			
			if (method != null) {
				
				plugin.getContactStorageService().deleteContactMethod(player, method.getAddress());
				
				player.sendMessage(
					FormatUtil.empty(),
					Texts.of(TextColors.GREEN, "Contact method ", TextColors.GOLD, contactMethod, TextColors.GREEN, " has been deleted.").builder().build()
				);
				
			} else {
				
				player.sendMessage(
					FormatUtil.empty(),
					Texts.of(TextColors.GOLD, "Contact method ", TextColors.RED, contactMethod, TextColors.GOLD, " was not found.").builder().build()
				);
				
			}
			
			return CommandResult.success();
			
		}
		
		if (delete && contactMethod.equals("")) {
			
			plugin.getContactStorageService().deleteContact(player);
			
			player.sendMessage(
				FormatUtil.empty(),
				Texts.of(TextColors.GREEN, "All contact information has been deleted.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (!delete && !cancel && !contactMethod.equals("")) {
			
			TextBuilder message = Texts.builder();
			
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(Texts.of(TextColors.WHITE, "Would you like to delte method ", TextColors.GOLD, contactMethod, TextColors.WHITE, "?"));
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(getConfirmDeleteContactMethodAction(contactMethod), Texts.of(" "), getCancelDeleteContactMethodAction(contactMethod));
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			
			player.sendMessage(message.build());
			
			return CommandResult.success();
			
		}
		
		if (!delete && !cancel && contactMethod.equals("")) {
			
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
	
	private Text getConfirmDeleteContactMethodAction(String contactMethod) {
		
		return Texts.builder("Yes, delete method " + contactMethod + ".")
			.onClick(TextActions.runCommand("/unregister -d " + contactMethod))
			.color(TextColors.GREEN)
			.style(TextStyles.UNDERLINE)
			.build();
		
	}
	
	private Text getCancelDeleteContactMethodAction(String contactMethod) {
		
		return Texts.builder("No, keep method " + contactMethod + ".")
			.onClick(TextActions.runCommand("/unregister -c " + contactMethod))
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

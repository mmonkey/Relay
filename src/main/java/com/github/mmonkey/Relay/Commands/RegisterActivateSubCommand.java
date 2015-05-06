package com.github.mmonkey.Relay.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;

public class RegisterActivateSubCommand extends RegisterCommand {

	public RegisterActivateSubCommand(Relay plugin) {
		super(plugin);
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
	
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String code = (args.hasAny("code")) ? ((String) args.getOne("code").get()) : "";
		Player player = (Player) src;
		Contact contact = getContact(player);
		
		if (code.equals("")) {
			
			player.sendMessage(
				Texts.of(TextColors.RED, "Code cannot be verified, try again.").builder()
				.build()
			);
			
			return CommandResult.success();
		}
			
		List<ContactMethod> methods = contact.getMethods();
		
		for (ContactMethod method: methods) {
			if (method.getActivationKey().equals(code)) {
				
				method.isActivated(true);
				method.setActivationKey("");
				
				contact.setMethods(methods);
				saveContactActivated(contact, player);
				
				player.sendMessage(
					Texts.of(TextColors.GREEN, "Your account has been activated!").builder()
					.build()
				);
				
				return CommandResult.success();
			}
		}
		
		player.sendMessage(
			Texts.of(TextColors.RED, "Code cannot be verified, try again.").builder()
			.build()
		);
		
		return CommandResult.success();
	
	}

}

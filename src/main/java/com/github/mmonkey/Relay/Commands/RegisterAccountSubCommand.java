package com.github.mmonkey.Relay.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.PaginatedList;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FormatUtil;

public class RegisterAccountSubCommand extends RegisterCommand {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		int page = (args.hasAny("page")) ? ((Integer) args.getOne("page").get()) : 1;
		
		Player player = (Player) src;
		EncryptionUtil encryptionUtil = getEncryptionUtil();
		PaginatedList pagination = new PaginatedList("/register account");
		pagination.setLineNumberType("");
		
		TextBuilder message = Texts.builder();
		TextBuilder header = Texts.builder();
		
		List<String> list = plugin.getContactStorageService().getContactMethodList(player);
		
		for (String name: list) {
			
			ContactMethod method = plugin.getContactStorageService().getContactMethod(player, name);
			
			if (method != null) {
				
				try {
				
					String type = (method.getType().equals(ContactMethodTypes.EMAIL)) ? "email": "phone";
					String address = encryptionUtil.decrypt(method.getAddress());
					
					TextBuilder row = Texts.builder();
					row.append(Texts.of(TextColors.WHITE, name + ": " + address + " - "));
					row.append(getEditAction(address, type, name));
					
					pagination.add(row.build());
				
				} catch (Exception e) {
					// Don't panic.
				}
				
			}
			
		}
		
		header.append(Texts.of(TextColors.GREEN, "-------"));
		header.append(Texts.of(TextColors.GREEN, " Showing contact methods page " + page + " of " + pagination.getTotalPages() + " "));
		header.append(Texts.of(TextColors.GREEN, "-------"));

		pagination.setHeader(header.build());
		
		message.append(FormatUtil.empty());
		message.append(pagination.getPage(page));
		
		player.sendMessage(message.build());
		
		return CommandResult.success();
		
	}
	
	private Text getEditAction(String displayName, String type, String name) {
		
		return Texts.builder("edit")
				.onClick(TextActions.runCommand("/register edit -t " + type + " " + name))
				.onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Edit ", TextColors.GOLD, displayName)))
				.color(TextColors.DARK_AQUA)
				.build();
		
	}
	
	public RegisterAccountSubCommand(Relay plugin) {
		super(plugin);
	}

}

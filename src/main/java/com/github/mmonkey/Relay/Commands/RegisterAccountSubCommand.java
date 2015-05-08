package com.github.mmonkey.Relay.Commands;

import java.util.ArrayList;
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

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.PaginatedList;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;

public class RegisterAccountSubCommand extends RegisterCommand {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		int page = (args.hasAny("page")) ? ((Integer) args.getOne("page").get()) : 1;
		
		Player player = (Player) src;
		Contact contact = getContact(player);
		EncryptionUtil encryptionUtil = getEncryptionUtil();
		PaginatedList pagination = new PaginatedList("/register account");
		
		TextBuilder message = Texts.builder();
		TextBuilder header = Texts.builder();
		
		//TODO get ContactMethodList
		List<String> list = new ArrayList<String>();
		
		for (String item: list) {
			
			//TODO get ContactMethod
			ContactMethod method = plugin.getContactStorageService().getContactMethod(item);
				
			try {
			
				String type = (method.getType().equals(ContactMethodTypes.EMAIL)) ? "email": "phone";
				String address = encryptionUtil.decrypt(method.getAddress());
				
				TextBuilder row = Texts.builder();
				row.append(Texts.of(TextColors.WHITE, address + " - "));
				row.append(getEditAction(address, type, "GET_METHOD_NAME"));
				
				pagination.add(row.build());
			
			} catch (Exception e) {
				// Don't panic.
			}
				
		}
		
		//TODO: finish displaying pagination 
		
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

package com.github.mmonkey.Relay.Commands;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.Carriers;
import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FormatUtil;

public class RegisterEmailSubcommand extends RegisterCommand {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean accept = (args.hasAny("a")) ? (Boolean) args.getOne("a").get() : false;
		boolean decline = (args.hasAny("d")) ? (Boolean) args.getOne("d").get() : false;
		String emailAddress = (args.hasAny("emailAddress")) ? ((String) args.getOne("emailAddress").get()) : "";
		
		Player player = (Player) src;
		EncryptionUtil encryptionUtil = getEncryptionUtil();
		boolean isValid = isValidEmailAddress(emailAddress);
		
		Contact contact = new Contact();
		List<String> contacts = plugin.getContactStorageService().getContactList();
		
		if (contacts.contains(player.getUniqueId().toString())) {
			contact = getContact(player);
			accept = contact.acceptTerms();
		}
		
		if (decline) {
			
			player.sendMessage(
				FormatUtil.empty(),
				Texts.of(TextColors.GOLD, "Registration process canceled, your information will not be stored.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		if (!accept && !decline) {
			
			player.sendMessage(getTermsAndConditions("email", emailAddress));
			return CommandResult.success();
			
		}
			
		if(!isValid) {
				
			player.sendMessage(
				Texts.of(TextColors.RED, "You must provide a valid email address to register!").builder()
				.build()
			);
			
			return CommandResult.success();
				
		}

		try {
			
			ContactMethod method = new ContactMethod(ContactMethodTypes.EMAIL, encryptionUtil.encrypt(emailAddress), Carriers.EMAIL, EncryptionUtil.generateSecretKey(4));
			this.saveContactNotActivated(contact, method, player);
			
		} catch (UnsupportedEncodingException  e) {
		
			e.printStackTrace();
			return CommandResult.empty();
		
		} catch (GeneralSecurityException e) {
			
			e.printStackTrace();
			return CommandResult.empty();
			
		}
		
		return CommandResult.success();
			
	}
	
	private boolean isValidEmailAddress(String email) {
		
		String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		
		return matcher.matches();
	
	}
	
	public RegisterEmailSubcommand(Relay plugin) {
		super(plugin);
	}

}

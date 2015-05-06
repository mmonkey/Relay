package com.github.mmonkey.Relay.Commands;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

public class RegisterPhoneSubCommand extends RegisterCommand {
	
	public RegisterPhoneSubCommand(Relay plugin) {
		super(plugin);
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean accept = (args.hasAny("a")) ? (Boolean) args.getOne("a").get() : false;
		boolean decline = (args.hasAny("d")) ? (Boolean) args.getOne("d").get() : false;
		String phoneNumber = (args.hasAny("phoneNumber")) ? getPhoneNumber((String) args.getOne("phoneNumber").get()) : "";
		String carrier = (args.hasAny("carrier")) ? ((String) args.getOne("carrier").get()) : "";
		
		Player player = (Player) src;
		Contact contact = getContact(player);
		EncryptionUtil encryptionUtil = getEncryptionUtil();
		boolean isValid = (phoneNumber != null && isValidPhoneNumber(phoneNumber));
		
		accept = contact.acceptTerms();
		decline = !contact.acceptTerms();
		
		if (decline) {
			
			player.sendMessage(
				Texts.of(TextColors.GOLD, "Registration process canceled, your information will not be stored.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		if (!accept && !decline) {
			
			player.sendMessage(getTermsAndConditions("phone", phoneNumber));
			return CommandResult.success();
			
		}
			
		if(!isValid) {
				
			player.sendMessage(
				Texts.of(TextColors.RED, "You must provide a valid phone number to register!").builder()
				.build()
			);
			
			return CommandResult.success();
				
		}
			
		if (!carrier.equals("")) {
			
			try {
				
				ContactMethod method = new ContactMethod(ContactMethodTypes.SMS, encryptionUtil.encrypt(phoneNumber), Carriers.valueOf(carrier.toUpperCase()), EncryptionUtil.generateSecretKey(4));

				this.saveContactNotActivated(contact, method, player);
				return CommandResult.success();
			
			} catch (IllegalArgumentException e) {
				
				// TODO invalid carrier, select carrier from list (with note to notify admin if carrier not on list)
				return CommandResult.empty();
				
			} catch (UnsupportedEncodingException  e) {
				
				e.printStackTrace();
				return CommandResult.empty();
			
			} catch (GeneralSecurityException e) {
				
				e.printStackTrace();
				return CommandResult.empty();
				
			}
			
		}
				
		//TODO:
		//prompt player to select carrier from list
		
		return CommandResult.success();
			
	}
	
	private String getPhoneNumber(String contact) {
		
		try {
			
			Pattern pattern = Pattern.compile("(?:\\d+\\s*)+");
			Matcher matcher = pattern.matcher(contact);
			
			if (matcher.find()) {
				
				return matcher.group(0);
		    
			} else {
		    
				return null;
		    
			}
		
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		    return null;
		}
		
	}
	
	private boolean isValidPhoneNumber(String phone) {
		
		try {
			
			Long phoneNumber = Long.parseLong(phone);
			if (phoneNumber > 10000000) {
				return true;
			}
			return false;
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		
	}

}

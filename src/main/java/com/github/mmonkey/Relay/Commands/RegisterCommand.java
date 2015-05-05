package com.github.mmonkey.Relay.Commands;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

import com.github.mmonkey.Relay.Carriers;
import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Services.SendActivationMessageRelayService;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FormatUtil;
import com.github.mmonkey.Relay.Utilities.StorageUtil;

public class RegisterCommand implements CommandExecutor {

	private static final Text TERMS_AND_CONDITIONS = Texts.of(TextColors.WHITE, "Standard data fees and text messaging rates "
			+ "may apply based on your plan with your mobile phone carrier. The developer of this plugin, or any other plugin "
			+ "that uses Relay may not be held accountable for:", CommandMessageFormatting.NEWLINE_TEXT, "Charges which may "
			+ "occur when recieving text messages.", CommandMessageFormatting.NEWLINE_TEXT, "Phone numbers or email addresses "
			+ "becoming public.", CommandMessageFormatting.NEWLINE_TEXT, "You may opt out of message delivery from this service "
			+ "by using the command:", CommandMessageFormatting.NEWLINE_TEXT, TextColors.GOLD, "/unregister");
	
	private Relay plugin;
	
	public RegisterCommand(Relay plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean accept = (args.hasAny("a")) ? (Boolean) args.getOne("a").get() : false;
		boolean decline = (args.hasAny("d")) ? (Boolean) args.getOne("d").get() : false;
		boolean hasCode = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
		String contact = (args.hasAny("contact")) ? ((String) args.getOne("contact").get()) : "";
		String code = (args.hasAny("code")) ? ((String) args.getOne("code").get()) : "";
		String carrier = (args.hasAny("carrier")) ? ((String) args.getOne("carrier").get()) : "";
		
		String secretKey = plugin.getDefaultConfigService().getConfig()
				.getNode(StorageUtil.CONFIG_NODE_SETTINGS, StorageUtil.CONFIG_NODE_SECRET_KEY).getString();
		
		EncryptionUtil encryptionUtil = new EncryptionUtil(secretKey);
		
		Player player = (Player) src;
		Contact c = new Contact();
		
		List<String> contacts = plugin.getContactStorageService().getContactList();
		if (contacts.contains(player.getUniqueId().toString())) {
			c = plugin.getContactStorageService().getContact(player);
			accept = c.acceptTerms();
			decline = !c.acceptTerms();
		}
		
		if (decline) {
			
			player.sendMessage(
				Texts.of(TextColors.GOLD, "Registration process canceled, your information will not be stored.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		if (hasCode && !code.equals("")) {
			
			List<ContactMethod> methods = c.getMethods();
			
			for (ContactMethod method: methods) {
				if (method.getActivationKey().equals(code)) {
					method.isActivated(true);
					method.setActivationKey("");
					this.saveContactActivated(c, method, player);
					
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
		
		if (hasCode) {
			
			player.sendMessage(
				Texts.of(TextColors.RED, "You must provide a valid code when using the -c flag!").builder()
				.build()
			);
			
			return CommandResult.success();

		}
		
		if (accept && !contact.equals("")) {
			
			String phone = getPhoneNumber(contact);
			String email = contact;
			
			boolean isPhone = (phone != null && isValidPhoneNumber(phone));
			boolean isEmail = isValidEmailAddress(email);
			
			if (!isPhone && !isEmail) {
				
				player.sendMessage(
					Texts.of(TextColors.RED, "You must provide a valid email address or phone number to register!").builder()
					.build()
				);
				
				return CommandResult.success();
				
			}
			
			if (isEmail) {
				
				try {
					
					ContactMethod method = new ContactMethod(ContactMethodTypes.EMAIL, encryptionUtil.encrypt(email), Carriers.NO_CARRIER, getActivationKey());

					this.saveContactNotActivated(c, method, player);
					return CommandResult.success();

				} catch (Exception e) {
					
					e.printStackTrace();
					return CommandResult.empty();
				
				}
				
			}
			
			if (isPhone && Carriers.valueOf(carrier) != null) {
				
				try {
					
					ContactMethod method = new ContactMethod(ContactMethodTypes.SMS, encryptionUtil.encrypt(phone), Carriers.valueOf(carrier), getActivationKey());

					this.saveContactNotActivated(c, method, player);
					return CommandResult.success();
					
				} catch (Exception e) {
					
					e.printStackTrace();
					return CommandResult.empty();
					
				}
				
			}
			
			if (isPhone) {
				
				//TODO:
				//prompt player to select carrier from list
				
				return CommandResult.success();
				
			}
			
		}
		
		if (!accept && !decline) {
			
			TextBuilder message = Texts.builder();
			
			message.append(FormatUtil.empty());
			message.append(Texts.of(TextColors.GOLD, "Registration terms and conditions:"));
			message.append(TERMS_AND_CONDITIONS);
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(Texts.of(TextColors.GRAY, "Accept by clicking yes, or using command: /register -a " + contact));
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
			message.append(getTermsAndConditionsLink(contact));
			
			player.sendMessage(message.build());
			
			return CommandResult.success();
		}
		
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
		
		} catch (PatternSyntaxException ex) {
		    return null;
		}
		
	}
	
	private boolean isValidPhoneNumber(String phone) {
		
		try {
			
			Integer.parseInt(phone);
			return true;
			
		} catch (NumberFormatException e) {
			return false;
		}
		
	}
	
	private boolean isValidEmailAddress(String email) {
		
		String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		
		return matcher.matches();
	
	}
	
	private String getActivationKey() {
		
		Random rand = new Random();
		String characters = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
		char[] key = new char[4];
		
		for(int i = 0; i < 4; i++) {
			key[i] = characters.charAt(rand.nextInt(characters.length()));
		}
		
		return new String(key);
	}
	
	public Text getTermsAndConditionsLink(String contact) {
		
		return Texts.builder("Yes, I accept.")
			.onClick(TextActions.runCommand("/register -a " + contact))
			.color(TextColors.GREEN)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	private void saveContactActivated(Contact contact, ContactMethod method, Player player) {
		
		if (!contact.getMethods().contains(method)) {
			contact.getMethods().add(method);
		}
		
		contact.acceptTerms(true);
		plugin.getContactStorageService().saveContact(player, contact);
		
	}
	
	private void saveContactNotActivated(Contact contact, ContactMethod method, Player player) {
		
		boolean sendActivationMessage = false;
		String activationKey = method.getActivationKey();
		
		if (!contact.getMethods().contains(method)) {
			contact.getMethods().add(method);
		}
		
		contact.acceptTerms(true);
		plugin.getContactStorageService().saveContact(player, contact);
		
		if (sendActivationMessage) {
			
			SendActivationMessageRelayService service = new SendActivationMessageRelayService(plugin);
			service.sendActivationMessage(player, "Please verify your contact information by entering the following command on our server:"
					+ " /register -c " + activationKey);
			
		}
		
		player.sendMessage(
			Texts.of(TextColors.GREEN, "Thank you for registering, you will receive an activation code shortly. Follow the"
					+ " instructions in the message to verify your credentials.").builder()
			.build()
		);
		
	}

}

package com.github.mmonkey.Relay.Commands;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

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

import com.github.mmonkey.Relay.Carriers;
import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FormatUtil;

public class RegisterEditSubCommand extends RegisterCommand {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
	
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean email = (args.hasAny("e")) ? (Boolean) args.getOne("e").get() : false;
		boolean phone = (args.hasAny("p")) ? (Boolean) args.getOne("p").get() : false;
		boolean resend = (args.hasAny("r")) ? (Boolean) args.getOne("r").get() : false;
		String contactMethod = (args.hasAny("contactMethod")) ? ((String) args.getOne("contactMethod").get()) : "";
		String carrier = (args.hasAny("carrier")) ? ((String) args.getOne("carrier").get()).toUpperCase() : "";
		
		Player player = (Player) src;
		EncryptionUtil encryptionUtil = getEncryptionUtil();
		
		ContactMethod method = plugin.getContactStorageService().getContactMethod(player, contactMethod);
		Contact contact = getContact(player);
		
		if (method == null) {

			player.sendMessage(
				Texts.of(TextColors.RED, "Contact method ", TextColors.GOLD, contactMethod, TextColors.RED, " not found.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (phone && !carrier.equals("") && method.getType().equals(ContactMethodTypes.SMS)) {

			try {
				
				for (ContactMethod m: contact.getMethods()) {
					
					if (m.getType().equals(ContactMethodTypes.SMS) && encryptionUtil.decrypt(m.getAddress()).equals(contactMethod)) {
						m.setCarrier(Carriers.valueOf(carrier.toUpperCase()));
						this.saveContact(contact, player);
					}
	
				}
				
			
			} catch (IllegalArgumentException e) {
				// TODO Invalid carrier
			} catch (UnsupportedEncodingException e) {
			} catch (GeneralSecurityException e) {
			}
			
			return CommandResult.success();

		}
		
		if (resend) {
			
			this.sendActivationMessage(contact, method, player);
			
			return CommandResult.success();
			
		}
		
		TextBuilder message = Texts.builder();
		
		message.append(FormatUtil.empty());
		message.append(Texts.of(TextColors.GREEN, "---------------"));
		message.append(Texts.of(TextColors.GREEN, " Editing Contact Method "));
		message.append(Texts.of(TextColors.GREEN, "---------------"));
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		
		try {
			
			if (email) {
				message.append(Texts.of(TextColors.WHITE, "Email: ", TextColors.GOLD, encryptionUtil.decrypt(method.getAddress())));
				message.append(CommandMessageFormatting.NEWLINE_TEXT);
			}
			
			if (phone) {
				message.append(Texts.of(TextColors.WHITE, "Phone: ", TextColors.GOLD, encryptionUtil.decrypt(method.getAddress())));
				message.append(CommandMessageFormatting.NEWLINE_TEXT);
			}
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		if (phone) {
			message.append(Texts.of(TextColors.WHITE, "Carrier: ", TextColors.GOLD, method.getCarrier().getDisplayName(), TextColors.WHITE, Texts.of(" - ")));
			message.append(getChangeCarrierAction(contactMethod));
			message.append(CommandMessageFormatting.NEWLINE_TEXT);
		}
		
		message.append(Texts.of(TextColors.WHITE, "Status: "));
		message.append(getMethodStatus(method, contactMethod));
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		
		message.append(Texts.of("                    "));
		message.append(getDeleteMethodAction(contactMethod));
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		
		message.append(Texts.of(TextColors.GRAY, "---------------------------------------------------"));
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		
		//TODO show commands.
		
		player.sendMessage(message.build());
		
		return CommandResult.success();
		
	}
	
	private Text getMethodStatus(ContactMethod method, String contactMethod) {
		
		Text resendActivation =  Texts.builder("Resend Activation")
			.onClick(TextActions.runCommand("/register edit -r " + contactMethod))
			.color(TextColors.DARK_AQUA)
			.build();
		
		TextBuilder methodStatus = Texts.builder();
		
		if (!method.isActivated()) {
			
			methodStatus.append(Texts.of(TextColors.RED, "Not Activated", TextColors.WHITE, " - "));
			methodStatus.append(resendActivation);
		
		} else {
			
			methodStatus.append(Texts.of(TextColors.GREEN, "Activated"));
			
		}
		
		return methodStatus.build();
	
	}
	
	private Text getDeleteMethodAction(String contactMethod) {
		
		return Texts.builder("Delete this Contact Method")
			.onClick(TextActions.runCommand("/unregister " + contactMethod))
			.color(TextColors.RED)
			.style(TextStyles.UNDERLINE)
			.build();
		
	}
	
	private Text getChangeCarrierAction(String phone) {
		
		return Texts.builder("Change Carrier")
			.onClick(TextActions.runCommand("/register carriers -u " + phone))
			.color(TextColors.RED)
			.build();
		
	}
	
	public RegisterEditSubCommand(Relay plugin) {
		super(plugin);
	}

}

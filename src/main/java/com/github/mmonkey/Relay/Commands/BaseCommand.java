package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Email.*;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Services.ActivationMessageRelayService;
import com.github.mmonkey.Relay.Services.DefaultConfigStorageService;
import com.github.mmonkey.Relay.Services.HTMLTemplatingService;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class BaseCommand implements CommandExecutor {
	
	protected static final Text TERMS_AND_CONDITIONS = Text.of(TextColors.WHITE, "Standard data fees and text messaging rates "
		+ "may apply based on your plan with your mobile phone carrier. The developer of this plugin may not be held accountable for:",
		Text.NEW_LINE, "Charges which may occur when receiving text messages.",
		Text.NEW_LINE, "Phone numbers or email addresses becoming public.",
		Text.NEW_LINE, "You may opt out of message delivery from this service by using the command:",
		Text.NEW_LINE, TextColors.GOLD, "/unregister");
	
	protected Relay plugin;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		return CommandResult.empty();
	}
	
	protected Contact getContact(Player player) {
		
		Contact contact = new Contact();
		List<String> contacts = plugin.getContactStorageService().getContactList();
		
		if (contacts.contains(player.getUniqueId().toString())) {
			contact = plugin.getContactStorageService().getContact(player);
		}
		
		return contact;
	}
	
	protected EncryptionUtil getEncryptionUtil() {
		
		String secretKey = plugin.getDefaultConfigService().getConfig()
				.getNode(DefaultConfigStorageService.SETTINGS, DefaultConfigStorageService.SECRET_KEY).getString();
		
		return new EncryptionUtil(secretKey);
		
	}
	
	protected Text getTermsAndConditions(String subCommand, String address) {
		
		Text accept =  Text.builder("Yes, I accept")
			.onClick(TextActions.runCommand("/register " + subCommand + " -a " + address))
			.color(TextColors.GREEN)
			.style(TextStyles.UNDERLINE)
			.build();
		
		Text decline =  Text.builder("No, I do not accept")
			.onClick(TextActions.runCommand("/register " + subCommand + " -d " + address))
			.color(TextColors.RED)
			.style(TextStyles.UNDERLINE)
			.build();
		
		Text.Builder message = Text.builder();
		
		message.append(FormatUtil.empty());
		message.append(Text.of(TextColors.GOLD, "Registration terms and conditions:"));
		message.append(Text.NEW_LINE);
		message.append(TERMS_AND_CONDITIONS);
		message.append(Text.NEW_LINE);
		message.append(Text.NEW_LINE);
		message.append(Text.of(TextColors.GRAY, "Accept by clicking yes, or using command: /register " + subCommand + " -a " + address));
		message.append(Text.NEW_LINE);
		message.append(Text.NEW_LINE);
		message.append(accept, Text.of("   "), decline);
		message.append(Text.NEW_LINE);
		
		return message.build();
	
	}
	
	protected void saveContact(Contact contact, Player player) {
		plugin.getContactStorageService().saveContact(player, contact);
	}
	
	protected void saveContactNotActivated(Contact contact, ContactMethod method, Player player) {
		
		boolean methodExists = false;
		
		for (ContactMethod m: contact.getMethods()) {
			if (m.getAddress().equals(method.getAddress())) {
				methodExists = true;
			}
		}
		
		if (!methodExists) {
			
			contact.getMethods().add(method);
			contact.acceptTerms(true);
			
			this.sendActivationMessage(contact, method, player);
		
		} else {
			
			player.sendMessage(
				Text.of(TextColors.GOLD, "This account has already been registered, to manage your accounts, use command: ",
					Text.NEW_LINE,
					Text.of(TextColors.GOLD, "/register account"))
			);
			
		}
		
	}
	
	protected String getTemplate(Collection<String> templates) {
		
		if (!templates.isEmpty()) {
		
			for (String template: templates) {
			
				String filename = template.split("\\.")[0];
				String templateName = filename + ".mustache";
				
				File file = new File(plugin.getTemplateDir(), templateName);
				
				if (file.isFile()) {
					return templateName;
				}
				
			}
		
		}
			
		return plugin.getDefaultTemplate();
		
	}
	
	protected void sendActivationMessage(Contact contact, ContactMethod method, Player player) {
		
		String activationKey = method.getActivationKey();
			
		ActivationMessageRelayService service = new ActivationMessageRelayService(plugin);
		HTMLTemplatingService templateService = new HTMLTemplatingService();
		templateService.setTemplateDirectory(plugin.getTemplateDir());
		
		EmailHeaderSection header = new EmailHeaderSection(plugin.getGame().getServer().getBoundAddress().get().getHostString());
		header.setServerAddress(plugin.getGame().getServer().getBoundAddress().get().getAddress().getHostAddress()
				+ ":" + plugin.getGame().getServer().getBoundAddress().get().getPort());
		header.setInvisibleIntroduction("Thank you for registering your contact information on our Minecraft server! To verify "
				+ "your contact information, please enter the following command on our server");
		
		EmailBodySection infoSection = new EmailBodySection();
		infoSection.addContent(new EmailContent(EmailContentTypes.SECTION_HEADLINE, "Please verify your contact information!"));
		infoSection.addContent(new EmailContent(EmailContentTypes.PARAGRAPH, "<br />Thank you for registering your "
				+ "contact information on our Minecraft server! To verify your contact information, please "
				+ "enter the following command on our server:"));
		
		EmailBodySection commandSection = new EmailBodySection();
		commandSection.setSectionBackgroundColor("#52894F");
		EmailContent command = new EmailContent(EmailContentTypes.HEADLINE, "/register activate " + activationKey);
		command.setTextColor("#FFFFFF");
		commandSection.addContent(command);
		
		EmailBodySection wrongAddress = new EmailBodySection();
		wrongAddress.addContent(new EmailContent(EmailContentTypes.PARAGRAPH, "If you did not register on our Minecraft server, or received this "
				+ "message by mistake, please disregard this message."));
		
		EmailMessage email = new EmailMessage();
		email.setHeaderSection(header);
		email.addBodySection(infoSection);
		email.addBodySection(commandSection);
		email.addBodySection(wrongAddress);
		
		String emailMessage = templateService.parse(plugin.getDefaultTemplate(), email);

		if (emailMessage == "") {
			emailMessage = null;
		}
		
		String smsMessage = "Please verify your contact information by entering the following command on our server:"
				+ " /register activate " + activationKey;
		
		plugin.getContactStorageService().saveContact(player, contact);
		
		service.sendActivationMessage(method, smsMessage, emailMessage);
	
		player.sendMessage(
			Text.of(TextColors.GREEN, "You will receive an activation code shortly. Follow the instructions in the "
					+ "message to verify your credentials.")
		);
		
	}
	
	protected EmailMessage getSendEmail(CommandSource src, String message) {
		
		EmailMessage email = new EmailMessage();
		
		EmailHeaderSection header = new EmailHeaderSection(plugin.getGame().getServer().getBoundAddress().get().getHostString());
		header.setServerAddress(plugin.getGame().getServer().getBoundAddress().get().getAddress().getHostAddress()
				+ ":" + plugin.getGame().getServer().getBoundAddress().get().getPort());
		
		if (src instanceof Player) {
			
			header.setInvisibleIntroduction("New message from: " + src.getName());
		
		} else {
		
			header.setInvisibleIntroduction("New message from: " + plugin.getGame().getServer().getBoundAddress().get().getHostString());
		
		}
		
		EmailBodySection messageSection = new EmailBodySection();
		messageSection.addContent(new EmailContent(EmailContentTypes.PARAGRAPH, message));
		
		email.setHeaderSection(header);
		email.addBodySection(messageSection);
		
		return email;
		
	}
	
	public BaseCommand(Relay plugin) {
		this.plugin = plugin;
	}

}

package com.github.mmonkey.Relay.Commands;

import java.io.File;
import java.util.Collection;
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

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Email.EmailBodySection;
import com.github.mmonkey.Relay.Email.EmailContent;
import com.github.mmonkey.Relay.Email.EmailContentTypes;
import com.github.mmonkey.Relay.Email.EmailHeaderSection;
import com.github.mmonkey.Relay.Email.EmailMessage;
import com.github.mmonkey.Relay.Services.ActivationMessageRelayService;
import com.github.mmonkey.Relay.Services.DefaultConfigStorageService;
import com.github.mmonkey.Relay.Services.HTMLTemplatingService;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FormatUtil;

public class BaseCommand implements CommandExecutor {
	
	protected static final Text TERMS_AND_CONDITIONS = Texts.of(TextColors.WHITE, "Standard data fees and text messaging rates "
		+ "may apply based on your plan with your mobile phone carrier. The developer of this plugin, or any other plugin "
		+ "that uses Relay may not be held accountable for:", CommandMessageFormatting.NEWLINE_TEXT, "Charges which may "
		+ "occur when recieving text messages.", CommandMessageFormatting.NEWLINE_TEXT, "Phone numbers or email addresses "
		+ "becoming public.", CommandMessageFormatting.NEWLINE_TEXT, "You may opt out of message delivery from this service "
		+ "by using the command:", CommandMessageFormatting.NEWLINE_TEXT, TextColors.GOLD, "/unregister");
	
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
		
		Text accept =  Texts.builder("Yes, I accept")
			.onClick(TextActions.runCommand("/register " + subCommand + " -a " + address))
			.color(TextColors.GREEN)
			.style(TextStyles.UNDERLINE)
			.build();
		
		Text decline =  Texts.builder("No, I do not accept")
			.onClick(TextActions.runCommand("/register " + subCommand + " -d " + address))
			.color(TextColors.RED)
			.style(TextStyles.UNDERLINE)
			.build();
		
		TextBuilder message = Texts.builder();
		
		message.append(FormatUtil.empty());
		message.append(Texts.of(TextColors.GOLD, "Registration terms and conditions:"));
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		message.append(TERMS_AND_CONDITIONS);
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		message.append(Texts.of(TextColors.GRAY, "Accept by clicking yes, or using command: /register " + subCommand + " -a " + address));
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		message.append(accept, Texts.of("   "), decline);
		message.append(CommandMessageFormatting.NEWLINE_TEXT);
		
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
				Texts.of(TextColors.GOLD, "This account has already been registered, to manage your accounts, use command: ",
						CommandMessageFormatting.NEWLINE_TEXT,
						Texts.of(TextColors.GOLD, "/register account")).builder()
				.build()
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
			Texts.of(TextColors.GREEN, "You will receive an activation code shortly. Follow the instructions in the "
					+ "message to verify your credentials.").builder()
			.build()
		);
		
	}
	
	public BaseCommand(Relay plugin) {
		this.plugin = plugin;
	}

}

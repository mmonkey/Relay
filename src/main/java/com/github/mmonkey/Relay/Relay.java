package com.github.mmonkey.Relay;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.github.mmonkey.Relay.Commands.RegisterActivateSubCommand;
import com.github.mmonkey.Relay.Commands.RegisterCarriersSubCommand;
import com.github.mmonkey.Relay.Commands.RegisterCommand;
import com.github.mmonkey.Relay.Commands.RegisterEmailSubCommand;
import com.github.mmonkey.Relay.Commands.RegisterPhoneSubCommand;
import com.github.mmonkey.Relay.Services.ContactStorageService;
import com.github.mmonkey.Relay.Services.DefaultConfigStorageService;
import com.github.mmonkey.Relay.Services.GatewayStorageService;
import com.github.mmonkey.Relay.Services.HTMLTemplatingService;
import com.github.mmonkey.Relay.Services.MessageRelayService;
import com.github.mmonkey.Relay.Services.RelayService;
import com.github.mmonkey.Relay.Services.TemplatingService;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.inject.Inject;

@Plugin(id = Relay.ID, name = Relay.NAME, version = Relay.VERSION)
public class Relay {
	
	public static final String ID = "Relay";
	public static final String NAME = "Relay";
	public static final String VERSION = "0.0.1";
	
	private Game game;
	private Optional<PluginContainer> pluginContainer;
	private static Logger logger;
	
	private DefaultConfigStorageService defaultConfigService;
	private GatewayStorageService gatewayStorageService;
	private ContactStorageService contactStorageService;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private File configDir;
	
	public Game getGame() {
		return this.game;
	}
	
	public Optional<PluginContainer> getPluginContainer() {
		return this.pluginContainer;
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public DefaultConfigStorageService getDefaultConfigService() {
		return this.defaultConfigService;
	}
	
	public GatewayStorageService getGatewayStorageService() {
		return this.gatewayStorageService;
	}
	
	public ContactStorageService getContactStorageService() {
		return this.contactStorageService;
	}
	
	public File getConfigDir() {
		return this.configDir;
	}
	
	@Subscribe
	public void onPreInit(PreInitializationEvent event) {
		
		this.game = event.getGame();
		this.pluginContainer = game.getPluginManager().getPlugin(Relay.NAME);
		Relay.logger = game.getPluginManager().getLogger(pluginContainer.get());
		
		getLogger().info(String.format("Starting up %s v%s.", Relay.NAME, Relay.VERSION));
		
		if (!this.configDir.isDirectory()) {
			this.configDir.mkdirs();
		}
		
		File templateDir = new File(this.configDir, "templates");
		
		if (!templateDir.isDirectory()) {
			templateDir.mkdirs();
		}
		
		saveTemplateFiles(templateDir);
		
		this.defaultConfigService = new DefaultConfigStorageService(this, this.configDir);
		this.gatewayStorageService = new GatewayStorageService(this, this.configDir);
		this.contactStorageService = new ContactStorageService(this, this.configDir);
		
		this.defaultConfigService.load();
		this.gatewayStorageService.load();
		this.contactStorageService.load();
		
		try {
			
			this.saveSensitiveData();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
			
	}
	
	@Subscribe
	public void init(InitializationEvent event) {
		
		try {
			
			this.game.getServiceManager().setProvider(this, RelayService.class, new MessageRelayService(this));
			this.game.getServiceManager().setProvider(this, TemplatingService.class, new HTMLTemplatingService());
		
		} catch (ProviderExistsException e) {
			
			e.printStackTrace();
		
		}
		
		HashMap<List<String>, CommandSpec> subcommands = new HashMap<List<String>, CommandSpec>();
		
		/**
		 * /register email [[-a:accept] [-d:decline]] <emailAddress>
		 */
		subcommands.put(Arrays.asList("email"), CommandSpec.builder()
			.setPermission("relay.register.email")
			.setDescription(Texts.of("Register your email address."))
			.setExtendedDescription(Texts.of("If registered, you can receive emails from this server."))
			.setExecutor(new RegisterEmailSubCommand(this))
			.setArguments(GenericArguments.flags().flag("a").flag("d").buildWith(GenericArguments.string(Texts.of("emailAddress"))))
			.build());
		
		/**
		 * /register phone [[-a:accept] [-d:decline]] <phoneNumber> [carrier]
		 */
		subcommands.put(Arrays.asList("phone"), CommandSpec.builder()
			.setPermission("relay.register.phone")
			.setDescription(Texts.of("Register your phone number."))
			.setExtendedDescription(Texts.of("If registered, you can receive text messages from this server."))
			.setExecutor(new RegisterPhoneSubCommand(this))
			.setArguments(GenericArguments.seq(
					GenericArguments.flags().flag("a").flag("d").buildWith(GenericArguments.string(Texts.of("phoneNumber"))),
					GenericArguments.optional(GenericArguments.string(Texts.of("carrier")))))
			.build());
		
		/**
		 * /register activate <code>
		 */
		subcommands.put(Arrays.asList("activate"), CommandSpec.builder()
			.setDescription(Texts.of("Activate your contact method."))
			.setExtendedDescription(Texts.of("Enter the code from your verification message to activate that contact method."))
			.setExecutor(new RegisterActivateSubCommand(this))
			.setArguments(GenericArguments.string(Texts.of("code")))
			.build());
		
		/**
		 * /register carriers [[-s:select] [-u:update]] [name]
		 */
		subcommands.put(Arrays.asList("carriers"), CommandSpec.builder()
			.setPermission("relay.register.phone")
			.setDescription(Texts.of("Supported Carriers"))
			.setExtendedDescription(Texts.of("View a list of supported phone carriers for receiving SMS messages."))
			.setExecutor(new RegisterCarriersSubCommand(this))
			.setArguments(GenericArguments.seq(
				GenericArguments.optional(GenericArguments.integer(Texts.of("page"))),
				GenericArguments.flags().flag("s").flag("u").buildWith(GenericArguments.optional(GenericArguments.string(Texts.of("phone"))))
			))
			.build());
		
		/**
		 * /register
		 */
		CommandSpec registerCommand = CommandSpec.builder()
			.setDescription(Texts.of("Register your email or phone number."))
			.setExtendedDescription(Texts.of("If registered, you can recieve emails or text messages from this server."))
			.setExecutor(new RegisterCommand(this))
			.setChildren(subcommands)
			.build();
		
		if (this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.SETTINGS, DefaultConfigStorageService.ENABLED).getBoolean()) {
			
			game.getCommandDispatcher().register(this, registerCommand, "register");
		
		}
		
	}
	
	private void saveTemplateFiles(File templateDir) {
		try {
			File libFolder = new File(Relay.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath(), "lib");
			File defaultTemplateSource = new File(libFolder, "default.mustache");
			File defaultTemplate = new File(templateDir, "default.mustache");
			
			
			if (!defaultTemplate.isFile()) {
				Files.copy(defaultTemplateSource, defaultTemplate);
			}
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveSensitiveData() throws Exception {

		CommentedConfigurationNode settingsConfig = this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.SETTINGS);
		CommentedConfigurationNode emailConfig = this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.EMAIL_ACCOUNT_INFO);
		CommentedConfigurationNode mandrillConfig = this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.MANDRILL_ACCOUNT_INFO);
		
		String name = emailConfig.getNode(DefaultConfigStorageService.EMAIL_NAME).getString();
		String emailAddress = emailConfig.getNode(DefaultConfigStorageService.EMAIL_ADDRESS).getString();
		String username = emailConfig.getNode(DefaultConfigStorageService.EMAIL_USERNAME).getString();
		String password = emailConfig.getNode(DefaultConfigStorageService.EMAIL_PASSWORD).getString();
		String host = emailConfig.getNode(DefaultConfigStorageService.EMAIL_HOST).getString();
		int port = emailConfig.getNode(DefaultConfigStorageService.EMAIL_PORT).getInt();
		boolean ssl = emailConfig.getNode(DefaultConfigStorageService.EMAIL_SSL).getBoolean();
		
		String mandrillUsername = mandrillConfig.getNode(DefaultConfigStorageService.MANDRILL_USERNAME).getString();
		String mandrillPassword = mandrillConfig.getNode(DefaultConfigStorageService.MANDRILL_PASSWORD).getString();
	
		Gateway gateway = new Gateway();
		Gateway mandrill = new Gateway();
		
		EncryptionUtil encryptionUtil = new EncryptionUtil(settingsConfig.getNode(DefaultConfigStorageService.SECRET_KEY).getString());
		
		if (!name.equals("") && !username.equals("") && !password.equals("") && !host.equals("") && port != 0) {
			
			gateway.setName(name);
			gateway.setEmailAddress(encryptionUtil.encrypt(emailAddress));
			gateway.setUsername(encryptionUtil.encrypt(username));
			gateway.setPassword(encryptionUtil.encrypt(password));
			gateway.setHost(host);
			gateway.setPort(port);
			gateway.sslEnabled(ssl);
			
			if (gateway.isValid()) {
				this.gatewayStorageService.saveGateway(gateway);
			}
			
		}
		
		if (!mandrillUsername.equals("") && !mandrillPassword.equals("")) {
			
			mandrill.setName("Mandrill");
			mandrill.setEmailAddress(encryptionUtil.encrypt(""));
			mandrill.setUsername(encryptionUtil.encrypt(mandrillUsername));
			mandrill.setPassword(encryptionUtil.encrypt(mandrillPassword));
			mandrill.setHost("smtp.mandrillapp.com");
			mandrill.setPort(587);
			mandrill.sslEnabled(true);
			
			if (mandrill.isValid()) {
				this.gatewayStorageService.saveGateway(mandrill);
			}
			
		}
		
		this.defaultConfigService.clearSensitiveData();
		
	}

}

package com.github.mmonkey.Relay;

import java.io.File;
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

import com.github.mmonkey.Relay.Commands.RegisterActivateSubcommand;
import com.github.mmonkey.Relay.Commands.RegisterCommand;
import com.github.mmonkey.Relay.Commands.RegisterEmailSubcommand;
import com.github.mmonkey.Relay.Commands.RegisterPhoneSubcommand;
import com.github.mmonkey.Relay.Commands.RelayAccountSubcommand;
import com.github.mmonkey.Relay.Commands.RelayCarriersSubcommand;
import com.github.mmonkey.Relay.Commands.RelayCommand;
import com.github.mmonkey.Relay.Commands.RelayEditSubcommand;
import com.github.mmonkey.Relay.Commands.RelaySendSubcommand;
import com.github.mmonkey.Relay.Commands.UnregisterCommand;
import com.github.mmonkey.Relay.Events.PlayerChangeNameListener;
import com.github.mmonkey.Relay.Services.ContactStorageService;
import com.github.mmonkey.Relay.Services.DefaultConfigStorageService;
import com.github.mmonkey.Relay.Services.GatewayStorageService;
import com.github.mmonkey.Relay.Services.HTMLTemplatingService;
import com.github.mmonkey.Relay.Services.MessageRelayService;
import com.github.mmonkey.Relay.Services.RelayService;
import com.github.mmonkey.Relay.Services.TemplatingService;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FileUtils;
import com.google.common.base.Optional;
import com.google.inject.Inject;

@Plugin(id = Relay.ID, name = Relay.NAME, version = Relay.VERSION)
public class Relay {
	
	protected static final String ID = "Relay";
	protected static final String NAME = "Relay";
	protected static final String VERSION = "1.0.1";
	
	private Game game;
	private Optional<PluginContainer> pluginContainer;
	private static Logger logger;
	
	private DefaultConfigStorageService defaultConfigService;
	private GatewayStorageService gatewayStorageService;
	private ContactStorageService contactStorageService;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private File configDir;
	
	private File templateDir;
	
	private String defaultTemplate;
	
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
	
	public File getTemplateDir() {
		return this.templateDir;
	}
	
	public String getDefaultTemplate() {
		return this.defaultTemplate;
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
		
		this.templateDir = new File(this.configDir, "templates");
		
		if (!this.templateDir.isDirectory()) {
			this.templateDir.mkdirs();
		}
		
		FileUtils.copyResourcesRecursively(this.getClass().getResource("/templates"), this.templateDir);
		
		this.defaultConfigService = new DefaultConfigStorageService(this, this.configDir);
		this.gatewayStorageService = new GatewayStorageService(this, this.configDir);
		this.contactStorageService = new ContactStorageService(this, this.configDir);
		
		this.defaultConfigService.load();
		this.gatewayStorageService.load();
		this.contactStorageService.load();
		
		this.loadDefaultTemplate();
		
		try {
			
			this.saveSensitiveData();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
			
	}
	
	@Subscribe
	public <T> void init(InitializationEvent event) {
		
		try {
			
			this.game.getServiceManager().setProvider(this, RelayService.class, new MessageRelayService<T>(this));
			this.game.getServiceManager().setProvider(this, TemplatingService.class, new HTMLTemplatingService());
		
		} catch (ProviderExistsException e) {
			
			e.printStackTrace();
		
		}
		
		HashMap<List<String>, CommandSpec> registerSubcommands = new HashMap<List<String>, CommandSpec>();
		HashMap<List<String>, CommandSpec> relaySubcommands = new HashMap<List<String>, CommandSpec>();
		
		/**
		 * /register email [[-a:accept] [-d:decline]] <emailAddress>
		 */
		registerSubcommands.put(Arrays.asList("email"), CommandSpec.builder()
			.setPermission("relay.register")
			.setDescription(Texts.of("Register your email address."))
			.setExtendedDescription(Texts.of("If registered, you can receive emails from this server."))
			.setExecutor(new RegisterEmailSubcommand(this))
			.setArguments(GenericArguments.flags().flag("a").flag("d").buildWith(GenericArguments.string(Texts.of("emailAddress"))))
			.build());
		
		/**
		 * /register phone [[-a:accept] [-d:decline]] <phoneNumber> [carrier]
		 */
		registerSubcommands.put(Arrays.asList("phone"), CommandSpec.builder()
			.setPermission("relay.register")
			.setDescription(Texts.of("Register your phone number."))
			.setExtendedDescription(Texts.of("If registered, you can receive text messages from this server."))
			.setExecutor(new RegisterPhoneSubcommand(this))
			.setArguments(GenericArguments.seq(
					GenericArguments.flags().flag("a").flag("d").buildWith(GenericArguments.string(Texts.of("phoneNumber"))),
					GenericArguments.optional(GenericArguments.string(Texts.of("carrier")))))
			.build());
		
		/**
		 * /register activate <code>
		 */
		registerSubcommands.put(Arrays.asList("activate"), CommandSpec.builder()
			.setPermission("relay.register")
			.setDescription(Texts.of("Activate your contact method."))
			.setExtendedDescription(Texts.of("Enter the code from your verification message to activate that contact method."))
			.setExecutor(new RegisterActivateSubcommand(this))
			.setArguments(GenericArguments.string(Texts.of("code")))
			.build());
		
		/**
		 * /register
		 */
		CommandSpec registerCommand = CommandSpec.builder()
			.setDescription(Texts.of("Register your email or phone number."))
			.setExtendedDescription(Texts.of("If registered, you can recieve emails or text messages from this server."))
			.setExecutor(new RegisterCommand(this))
			.setChildren(registerSubcommands)
			.build();
		
		/**
		 * /relay carriers [[-s:select] [-u:update]] [name]
		 */
		relaySubcommands.put(Arrays.asList("carriers"), CommandSpec.builder()
			.setPermission("relay.register")
			.setDescription(Texts.of("Supported Carriers"))
			.setExtendedDescription(Texts.of("View a list of supported phone carriers for receiving SMS messages."))
			.setExecutor(new RelayCarriersSubcommand(this))
			.setArguments(GenericArguments.seq(
				GenericArguments.optional(GenericArguments.integer(Texts.of("page"))),
				GenericArguments.flags().flag("s").flag("u").buildWith(GenericArguments.optional(GenericArguments.string(Texts.of("phone"))))
			))
			.build());
		
		/**
		 * /relay account [page]
		 */
		relaySubcommands.put(Arrays.asList("account"), CommandSpec.builder()
			.setPermission("relay.register")
			.setDescription(Texts.of("Manage your contact methods."))
			.setExtendedDescription(Texts.of("View a list of your contact methods."))
			.setExecutor(new RelayAccountSubcommand(this))
			.setArguments(GenericArguments.optional(GenericArguments.integer(Texts.of("page"))))
			.build());
		
		/**
		 * /relay edit [-e][-p] <contactMethodId> [-c] [carrier]
		 */
		relaySubcommands.put(Arrays.asList("edit"), CommandSpec.builder()
			.setPermission("relay.register")
			.setDescription(Texts.of("Edit a contact method."))
			.setExtendedDescription(Texts.of("Edit contact method of the given contact method."))
			.setExecutor(new RelayEditSubcommand(this))
			.setArguments(GenericArguments.seq(
				GenericArguments.flags().flag("e").flag("p").flag("r").buildWith(GenericArguments.string(Texts.of("contactMethod"))),
				GenericArguments.flags().flag("c").buildWith(GenericArguments.optional(GenericArguments.string(Texts.of("carrier"))))
			))
			.build());
		
		/**
		 * /relay send [[-p] [player]] [[-t] [template]] <message>
		 */
		relaySubcommands.put(Arrays.asList("send"), CommandSpec.builder()
			.setPermission("relay.send")
			.setDescription(Texts.of("Send an email or sms message."))
			.setExtendedDescription(Texts.of("If the player(s) have a relay account, send them an email or sms message."))
			.setExecutor(new RelaySendSubcommand(this))
			.setArguments(
				GenericArguments.flags()
					.valueFlag(GenericArguments.string(Texts.of("player")), "p")
					.valueFlag(GenericArguments.string(Texts.of("template")), "t")
					.buildWith(GenericArguments.remainingJoinedStrings(Texts.of("message")))
			)
			.build());
		
		/**
		 * /relay send [[-p] [player]] [[-t] [template]] <message>
		 */
		relaySubcommands.put(Arrays.asList("sendall", "all"), CommandSpec.builder()
			.setPermission("relay.sendall")
			.setDescription(Texts.of("Send an email or sms message to all."))
			.setExtendedDescription(Texts.of("Send all contacts an email or sms message."))
			.setExecutor(new RelaySendSubcommand(this))
			.setArguments(
				GenericArguments.flags()
					.valueFlag(GenericArguments.string(Texts.of("template")), "t")
					.buildWith(GenericArguments.remainingJoinedStrings(Texts.of("message")))
			)
			.build());
		
		/**
		 * /relay
		 */
		CommandSpec relayCommand = CommandSpec.builder()
			.setDescription(Texts.of("Relay edit, carriers, account, send"))
			.setExtendedDescription(Texts.of("Manage your relay account or send an email or sms message."))
			.setExecutor(new RelayCommand(this))
			.setChildren(relaySubcommands)
			.build();
		
		/**
		 * /unregister
		 */
		CommandSpec unregisterCommand = CommandSpec.builder()
			.setPermission("relay.register")
			.setDescription(Texts.of("Unregister your contact methods."))
			.setExtendedDescription(Texts.of("Remove all contact information from this server."))
			.setExecutor(new UnregisterCommand(this))
			.setArguments(
				GenericArguments.flags().flag("d").flag("c").buildWith(GenericArguments.optional(GenericArguments.string(Texts.of("method"))))
			)
			.build();
		
		if (this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.SETTINGS, DefaultConfigStorageService.ENABLED).getBoolean()) {
			
			game.getCommandDispatcher().register(this, registerCommand, "register");
			game.getCommandDispatcher().register(this, relayCommand, "relay");
			game.getCommandDispatcher().register(this, unregisterCommand, "unregister");
		
		}
		
		game.getEventManager().register(this, new PlayerChangeNameListener(this));
		
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
	
	private void loadDefaultTemplate() {
		
		this.defaultTemplate = this.getDefaultConfigService().getConfig()
			.getNode(DefaultConfigStorageService.MESSAGES, DefaultConfigStorageService.EMAIL_TEMPLATE).getString();
		
	}

}

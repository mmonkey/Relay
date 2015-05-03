package com.github.mmonkey.Relay;

import java.io.File;

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

import com.github.mmonkey.Relay.Services.ContactStorageService;
import com.github.mmonkey.Relay.Services.DefaultConfigStorageService;
import com.github.mmonkey.Relay.Services.GatewayStorageService;
import com.github.mmonkey.Relay.Services.MessageRelayService;
import com.github.mmonkey.Relay.Services.RelayService;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.StorageUtil;
import com.google.common.base.Optional;
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
	
	@Subscribe
	public void onPreInit(PreInitializationEvent event) {
		
		this.game = event.getGame();
		this.pluginContainer = game.getPluginManager().getPlugin(Relay.NAME);
		Relay.logger = game.getPluginManager().getLogger(pluginContainer.get());
		
		getLogger().info(String.format("Starting up %s v%s.", Relay.NAME, Relay.VERSION));
		
		if (!this.configDir.isDirectory()) {
			this.configDir.mkdirs();
		}
		
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
	public void onInit(InitializationEvent event) {
		
		try {
			
			this.game.getServiceManager().setProvider(this, RelayService.class, new MessageRelayService(this));
		
		} catch (ProviderExistsException e) {
			
			e.printStackTrace();
		
		}
	}
	
	private void saveSensitiveData() throws Exception {

		CommentedConfigurationNode settingsConfig = this.getDefaultConfigService().getConfig().getNode(StorageUtil.CONFIG_NODE_SETTINGS);
		CommentedConfigurationNode emailConfig = this.getDefaultConfigService().getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO);
		CommentedConfigurationNode mandrillConfig = this.getDefaultConfigService().getConfig().getNode(StorageUtil.CONFIG_NODE_MANDRILL_ACCOUNT_INFO);
		
		String name = emailConfig.getNode(StorageUtil.CONFIG_NODE_EMAIL_NAME).getString();
		String emailAddress = emailConfig.getNode(StorageUtil.CONFIG_NODE_EMAIL_ADDRESS).getString();
		String username = emailConfig.getNode(StorageUtil.CONFIG_NODE_EMAIL_USERNAME).getString();
		String password = emailConfig.getNode(StorageUtil.CONFIG_NODE_EMAIL_PASSWORD).getString();
		String host = emailConfig.getNode(StorageUtil.CONFIG_NODE_EMAIL_HOST).getString();
		int port = emailConfig.getNode(StorageUtil.CONFIG_NODE_EMAIL_PORT).getInt();
		boolean ssl = emailConfig.getNode(StorageUtil.CONFIG_NODE_EMAIL_SSL).getBoolean();
		
		String mandrillUsername = mandrillConfig.getNode(StorageUtil.CONFIG_NODE_MANDRILL_USERNAME).getString();
		String mandrillPassword = mandrillConfig.getNode(StorageUtil.CONFIG_NODE_MANDRILL_PASSWORD).getString();
	
		Gateway gateway = new Gateway();
		Gateway mandrill = new Gateway();
		
		EncryptionUtil encryptionUtil = new EncryptionUtil(settingsConfig.getNode(StorageUtil.CONFIG_NODE_SECRET_KEY).getString());
		
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
		
		mandrill.setName("Mandrill");
		mandrill.setUsername(encryptionUtil.encrypt(mandrillUsername));
		mandrill.setPassword(encryptionUtil.encrypt(mandrillPassword));
		mandrill.setHost("smtp.mandrillapp.com");
		mandrill.setPort(587);
		mandrill.sslEnabled(true);
		
		if (mandrill.isValid()) {
			this.gatewayStorageService.saveGateway(mandrill);
		}
		
		this.defaultConfigService.clearSensitiveData();
		
	}

}

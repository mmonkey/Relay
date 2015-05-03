package com.github.mmonkey.Relay;

import java.io.File;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;

import com.github.mmonkey.Relay.Services.DefaultConfigStorageService;
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
		this.defaultConfigService.load();
			
	}

}

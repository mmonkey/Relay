package com.github.mmonkey.Relay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import com.github.mmonkey.Relay.Relay;

public class StorageService {
	
	protected static final String LIST = "list";

	protected Relay plugin;
	private File configDir;
	private File configFile;
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode config;
	
	protected Relay getPlugin() {
		return this.plugin;
	}
	
	protected File getConfigDir() {
		return this.configDir;
	}
	
	protected File getConfigFile() {
		return this.configFile;
	}
	
	protected void setConfigFile(File configFile) {
		this.configFile = configFile;
	}
	
	protected ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
		return this.configLoader;
	}
	
	protected void setConfigLoader(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
		this.configLoader = configLoader;
	}
	
	protected CommentedConfigurationNode getConfig() {
		return this.config;
	}
	
	protected void setConfig(CommentedConfigurationNode config) {
		this.config = config;
	}
	
	protected void load() {
		
		setConfigLoader(HoconConfigurationLoader.builder().setFile(getConfigFile()).build());
		
		try {
			
			if (!getConfigFile().isFile()) {
				getConfigFile().createNewFile();
			}
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
	}
	
	protected void saveConfig() {
		
		try {
			
			this.getConfigLoader().save(this.getConfig());
		
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	protected List<String> getList(CommentedConfigurationNode config) {
		
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) config.getNode(LIST).getValue();
		
		if (list == null) {
			return new ArrayList<String>();
		}
		
		return list;
		
	}
	
	protected StorageService(Relay plugin, File configDir) {
		
		this.plugin = plugin;
		this.configDir = configDir;
		
	}
	
}

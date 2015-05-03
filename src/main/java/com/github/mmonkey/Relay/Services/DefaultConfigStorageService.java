package com.github.mmonkey.Relay.Services;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.StorageUtil;

public class DefaultConfigStorageService extends StorageService {

	public DefaultConfigStorageService(Relay plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, Relay.NAME + ".conf"));
	}

	@Override
	public void load() {
		
		setConfigLoader(HoconConfigurationLoader.builder().setFile(getConfigFile()).build());
		
		try {
			
			if (!getConfigFile().isFile()) {
				getConfigFile().createNewFile();
				save();
			}
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
		
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
			
		}
		
	}

	public void save() throws NoSuchAlgorithmException {
		
		try {
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_NAME).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_USERNAME).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_ADDRESS).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_PASSWORD).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_HOST).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_PORT).setValue(0);
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_SSL).setValue(true);
		
		getConfig().getNode(StorageUtil.CONFIG_NODE_MANDRILL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_MANDRILL_USERNAME).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_MANDRILL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_MANDRILL_PASSWORD).setValue("");
		
		getConfig().getNode(StorageUtil.CONFIG_NODE_MESSAGES, StorageUtil.CONFIG_NODE_EMAIL_DISPLAY_NAME).setValue("Minecraft Server");
		getConfig().getNode(StorageUtil.CONFIG_NODE_MESSAGES, StorageUtil.CONFIG_NODE_EMAIL_SUBJECT).setValue("You have a new alert from your Minecraft server!");
		getConfig().getNode(StorageUtil.CONFIG_NODE_MESSAGES, StorageUtil.CONFIG_NODE_SMS_SUBJECT).setValue("MC Alert!");
		
		getConfig().getNode(StorageUtil.CONFIG_NODE_SETTINGS, StorageUtil.CONFIG_NODE_ENABLED).setValue(true);
		getConfig().getNode(StorageUtil.CONFIG_NODE_SETTINGS, StorageUtil.CONFIG_NODE_SECRET_KEY).setValue(EncryptionUtil.generateSecretKey());
		
		saveConfig();
		
	}
	
	public void clearSensitiveData() {
		
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_NAME).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_ADDRESS).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_USERNAME).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_PASSWORD).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_HOST).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_PORT).setValue(0);
		getConfig().getNode(StorageUtil.CONFIG_NODE_EMAIL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_EMAIL_SSL).setValue(true);
		
		getConfig().getNode(StorageUtil.CONFIG_NODE_MANDRILL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_MANDRILL_USERNAME).setValue("");
		getConfig().getNode(StorageUtil.CONFIG_NODE_MANDRILL_ACCOUNT_INFO, StorageUtil.CONFIG_NODE_MANDRILL_PASSWORD).setValue("");
		
		saveConfig();
	}
	
}

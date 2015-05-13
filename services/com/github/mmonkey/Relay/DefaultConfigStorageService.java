package com.github.mmonkey.Relay;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import com.github.mmonkey.Relay.EncryptionUtil;
import com.github.mmonkey.Relay.Relay;

public class DefaultConfigStorageService extends StorageService {
	
	protected static final String EMAIL_ACCOUNT_INFO = "emailAccountInfo";
	protected static final String EMAIL_NAME = "accountName";
	protected static final String EMAIL_ADDRESS = "emailAddress";
	protected static final String EMAIL_USERNAME = "username";
	protected static final String EMAIL_PASSWORD = "password";
	protected static final String EMAIL_HOST = "host";
	protected static final String EMAIL_PORT = "port";
	protected static final String EMAIL_SSL = "useSSL";
	
	protected static final String MANDRILL_ACCOUNT_INFO = "mandrillAccountInfo";
	protected static final String MANDRILL_USERNAME = "username";
	protected static final String MANDRILL_PASSWORD = "password";
	
	protected static final String MESSAGES = "messages";
	protected static final String EMAIL_DISPLAY_NAME = "displayName";
	protected static final String EMAIL_SUBJECT = "emailSubjectLine";
	protected static final String EMAIL_TEMPLATE = "emailTemplate";
	protected static final String SMS_SUBJECT = "smsSubjectLine";
	
	protected static final String SETTINGS = "settings";
	protected static final String ENABLED = "enabled";
	protected static final String SECRET_KEY = "secretKey";

	@Override
	protected void load() {
		
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

	protected void save() throws NoSuchAlgorithmException {
		
		try {
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_NAME).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_USERNAME).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_ADDRESS).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_PASSWORD).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_HOST).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_PORT).setValue(0);
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_SSL).setValue(true);
		
		getConfig().getNode(MANDRILL_ACCOUNT_INFO, MANDRILL_USERNAME).setValue("");
		getConfig().getNode(MANDRILL_ACCOUNT_INFO, MANDRILL_PASSWORD).setValue("");
		
		getConfig().getNode(MESSAGES, EMAIL_DISPLAY_NAME).setValue("Minecraft Server");
		getConfig().getNode(MESSAGES, EMAIL_SUBJECT).setValue("You have a new alert from your Minecraft server!");
		getConfig().getNode(MESSAGES, EMAIL_TEMPLATE).setValue("default.mustache");
		getConfig().getNode(MESSAGES, SMS_SUBJECT).setValue("MC Alert!");
		
		getConfig().getNode(SETTINGS, ENABLED).setValue(true);
		getConfig().getNode(SETTINGS, SECRET_KEY).setValue(EncryptionUtil.generateSecretKey(16))
			.setComment("Warning: Do not change this value!");
		
		saveConfig();
		
	}
	
	protected void clearSensitiveData() {
		
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_NAME).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_ADDRESS).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_USERNAME).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_PASSWORD).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_HOST).setValue("");
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_PORT).setValue(0);
		getConfig().getNode(EMAIL_ACCOUNT_INFO, EMAIL_SSL).setValue(true);
		
		getConfig().getNode(MANDRILL_ACCOUNT_INFO, MANDRILL_USERNAME).setValue("");
		getConfig().getNode(MANDRILL_ACCOUNT_INFO, MANDRILL_PASSWORD).setValue("");
		
		saveConfig();
	}
	
	protected DefaultConfigStorageService(Relay plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, "config.conf"));
		
	}
	
}

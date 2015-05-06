package com.github.mmonkey.Relay.Services;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;

public class DefaultConfigStorageService extends StorageService {
	
	public static final String EMAIL_ACCOUNT_INFO = "emailAccountInfo";
	public static final String EMAIL_NAME = "accountName";
	public static final String EMAIL_ADDRESS = "emailAddress";
	public static final String EMAIL_USERNAME = "username";
	public static final String EMAIL_PASSWORD = "password";
	public static final String EMAIL_HOST = "host";
	public static final String EMAIL_PORT = "port";
	public static final String EMAIL_SSL = "useSSL";
	
	public static final String MANDRILL_ACCOUNT_INFO = "mandrillAccountInfo";
	public static final String MANDRILL_USERNAME = "username";
	public static final String MANDRILL_PASSWORD = "password";
	
	public static final String MESSAGES = "messages";
	public static final String EMAIL_DISPLAY_NAME = "displayName";
	public static final String EMAIL_SUBJECT = "emailSubjectLine";
	public static final String SMS_SUBJECT = "smsSubjectLine";
	
	public static final String SETTINGS = "settings";
	public static final String ENABLED = "enabled";
	public static final String SECRET_KEY = "secretKey";

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
		getConfig().getNode(MESSAGES, SMS_SUBJECT).setValue("MC Alert!");
		
		getConfig().getNode(SETTINGS, ENABLED).setValue(true);
		getConfig().getNode(SETTINGS, SECRET_KEY).setValue(EncryptionUtil.generateSecretKey(16))
			.setComment("Warning: Do not change this value!");
		
		saveConfig();
		
	}
	
	public void clearSensitiveData() {
		
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
	
	public DefaultConfigStorageService(Relay plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, Relay.NAME + ".conf"));
	}
	
}

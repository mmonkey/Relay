package com.github.mmonkey.Relay.Services;

import java.io.File;
import java.util.List;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import com.github.mmonkey.Relay.Gateway;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.StorageUtil;

public class GatewayStorageService extends StorageService {

	public GatewayStorageService(Relay plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, "gateways.conf"));
	}
	
	public List<String> getGatewayList() {
		return getList(getConfig());
	}
	
	public Gateway getGateway(String name) {
		
		CommentedConfigurationNode config = getConfig().getNode(name);
		
		Gateway gateway = new Gateway();
		gateway.setName(name);
		gateway.setEmailAddress(config.getNode(StorageUtil.CONFIG_NODE_EMAIL_ADDRESS).getString());
		gateway.setUsername(config.getNode(StorageUtil.CONFIG_NODE_EMAIL_USERNAME).getString());
		gateway.setPassword(config.getNode(StorageUtil.CONFIG_NODE_EMAIL_PASSWORD).getString());
		gateway.setHost(config.getNode(StorageUtil.CONFIG_NODE_EMAIL_HOST).getString());
		gateway.setPort(config.getNode(StorageUtil.CONFIG_NODE_EMAIL_PORT).getInt());
		gateway.sslEnabled(config.getNode(StorageUtil.CONFIG_NODE_EMAIL_SSL).getBoolean());
		
		if (gateway.isValid()) {
			return gateway;
		}
		
		return null;
	}
	
	public void saveGateway(Gateway gateway) {
		
		List<String> list = getList(getConfig());
		
		if (!list.contains(gateway.getName())) {
			list.add(gateway.getName());
			getConfig().getNode(StorageUtil.CONFIG_NODE_LIST).setValue(list);
		}
		
		getConfig().getNode(gateway.getName(), StorageUtil.CONFIG_NODE_EMAIL_ADDRESS).setValue(gateway.getEmailAddress());
		getConfig().getNode(gateway.getName(), StorageUtil.CONFIG_NODE_EMAIL_USERNAME).setValue(gateway.getUsername());
		getConfig().getNode(gateway.getName(), StorageUtil.CONFIG_NODE_EMAIL_PASSWORD).setValue(gateway.getPassword());
		getConfig().getNode(gateway.getName(), StorageUtil.CONFIG_NODE_EMAIL_HOST).setValue(gateway.getHost());
		getConfig().getNode(gateway.getName(), StorageUtil.CONFIG_NODE_EMAIL_PORT).setValue(gateway.getPort());
		getConfig().getNode(gateway.getName(), StorageUtil.CONFIG_NODE_EMAIL_SSL).setValue(gateway.sslEnabled());
		
		saveConfig();
		
	}

}

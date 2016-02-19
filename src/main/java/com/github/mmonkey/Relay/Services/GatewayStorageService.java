package com.github.mmonkey.Relay.Services;

import com.github.mmonkey.Relay.Gateway;
import com.github.mmonkey.Relay.Relay;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.io.File;
import java.util.List;

public class GatewayStorageService extends StorageService {

    public List<String> getGatewayList() {
        return getList(getConfig());
    }

    public Gateway getGateway(String name) {

        CommentedConfigurationNode config = getConfig().getNode(name);

        Gateway gateway = new Gateway();
        gateway.setName(name);
        gateway.setEmailAddress(config.getNode(DefaultConfigStorageService.EMAIL_ADDRESS).getString());
        gateway.setUsername(config.getNode(DefaultConfigStorageService.EMAIL_USERNAME).getString());
        gateway.setPassword(config.getNode(DefaultConfigStorageService.EMAIL_PASSWORD).getString());
        gateway.setHost(config.getNode(DefaultConfigStorageService.EMAIL_HOST).getString());
        gateway.setPort(config.getNode(DefaultConfigStorageService.EMAIL_PORT).getInt());
        gateway.sslEnabled(config.getNode(DefaultConfigStorageService.EMAIL_SSL).getBoolean());

        if (gateway.isValid()) {
            return gateway;
        }

        return null;
    }

    public void saveGateway(Gateway gateway) {

        List<String> list = getList(getConfig());

        if (!list.contains(gateway.getName())) {
            list.add(gateway.getName());
            getConfig().getNode(LIST).setValue(list);
        }

        getConfig().getNode(gateway.getName(), DefaultConfigStorageService.EMAIL_ADDRESS).setValue(gateway.getEmailAddress());
        getConfig().getNode(gateway.getName(), DefaultConfigStorageService.EMAIL_USERNAME).setValue(gateway.getUsername());
        getConfig().getNode(gateway.getName(), DefaultConfigStorageService.EMAIL_PASSWORD).setValue(gateway.getPassword());
        getConfig().getNode(gateway.getName(), DefaultConfigStorageService.EMAIL_HOST).setValue(gateway.getHost());
        getConfig().getNode(gateway.getName(), DefaultConfigStorageService.EMAIL_PORT).setValue(gateway.getPort());
        getConfig().getNode(gateway.getName(), DefaultConfigStorageService.EMAIL_SSL).setValue(gateway.sslEnabled());

        saveConfig();

    }

    public GatewayStorageService(Relay plugin, File configDir) {
        super(plugin, configDir);

        setConfigFile(new File(configDir, "gateways.conf"));

    }

}

package com.github.mmonkey.Relay.Services;

import com.github.mmonkey.Relay.Relay;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageService {

    protected static final String LIST = "list";

    protected Relay plugin;
    private File configDir;
    private File configFile;
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private CommentedConfigurationNode config;

    public Relay getPlugin() {
        return this.plugin;
    }

    public File getConfigDir() {
        return this.configDir;
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        return this.configLoader;
    }

    public void setConfigLoader(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        this.configLoader = configLoader;
    }

    public CommentedConfigurationNode getConfig() {
        return this.config;
    }

    public void setConfig(CommentedConfigurationNode config) {
        this.config = config;
    }

    public void load() {

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

    public void saveConfig() {

        try {

            this.getConfigLoader().save(this.getConfig());

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public List<String> getList(CommentedConfigurationNode config) {

        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) config.getNode(LIST).getValue();

        if (list == null) {
            return new ArrayList<String>();
        }

        return list;

    }

    public StorageService(Relay plugin, File configDir) {

        this.plugin = plugin;
        this.configDir = configDir;

    }

}

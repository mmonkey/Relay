package com.github.mmonkey.Relay.Services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.StorageUtil;

public class ContactStorageService extends StorageService {

	public ContactStorageService(Relay plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, "contacts.conf"));
	}
	
	private void saveContactMethods(CommentedConfigurationNode config, Contact contact) {
		
		for (Map.Entry<ContactMethodTypes, String> method : contact.getMethods().entrySet()) {
			config.getNode(method.getKey().name()).setValue(method.getValue());
		}
		
	}
	
	private void saveBlacklist(CommentedConfigurationNode config, Contact contact) {
		
		List<String> list = new ArrayList<String>();
		
		for (UUID uniqueId: contact.getBlacklist()) {
			list.add(uniqueId.toString());
		}
		
		config.setValue(list);
		
	}
	
	public void saveContact(Player player, Contact contact) {
		
		List<String> list = getList(getConfig());
		
		if (!list.contains(player.getUniqueId().toString())) {
			list.add(player.getUniqueId().toString());
			getConfig().getNode(StorageUtil.CONFIG_NODE_LIST).setValue(list);
		}
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		config.getNode(StorageUtil.CONFIG_NODE_CONTACT_ACCEPT_TERMS).setValue(contact.acceptTerms());
		
		CommentedConfigurationNode methods = config.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHODS);
		saveContactMethods(methods, contact);
		
		CommentedConfigurationNode blacklist = config.getNode(StorageUtil.CONFIG_NODE_CONTACT_BLACKLIST);
		saveBlacklist(blacklist, contact);
		
		saveConfig();
	
	}
	
	private HashMap<ContactMethodTypes, String> getMethods(CommentedConfigurationNode config) {
		
		HashMap<ContactMethodTypes, String> methods = new HashMap<ContactMethodTypes, String>();
		
		for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry: config.getChildrenMap().entrySet()) {
			methods.put(ContactMethodTypes.valueOf((String) entry.getKey()), entry.getValue().getString());
		}
		
		return methods;
		
	}
	
	private List<UUID> getBlacklist(CommentedConfigurationNode config) {
		
		List<UUID> blacklist = new ArrayList<UUID>();
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) config.getValue();
		
		for (String uniqueId: list) {
			blacklist.add(UUID.fromString(uniqueId));
		}
		
		return blacklist;
		
	}
	
	public Contact getContact(Player player) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		CommentedConfigurationNode methodConfig = config.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHODS);
		CommentedConfigurationNode blacklistConfig = config.getNode(StorageUtil.CONFIG_NODE_CONTACT_BLACKLIST);
		
		Contact contact = new Contact();
		contact.acceptTerms(config.getNode(StorageUtil.CONFIG_NODE_CONTACT_ACCEPT_TERMS).getBoolean());
		contact.setMethods(getMethods(methodConfig));
		contact.setBlacklist(getBlacklist(blacklistConfig));
	
		return contact;
		
	}

}

package com.github.mmonkey.Relay.Services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Relay.Carriers;
import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.ContactMethodTypes;
import com.github.mmonkey.Relay.Utilities.StorageUtil;

public class ContactStorageService extends StorageService {

	public ContactStorageService(Relay plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, "contacts.conf"));
	}
	
	private void saveContactMethods(CommentedConfigurationNode config, Contact contact) {
		
		List<String> list = new ArrayList<String>();
		
		for (ContactMethod method: contact.getMethods()) {
			CommentedConfigurationNode item = config.getNode(method.getCarrier().getDisplayName());
			item.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHOD_TYPE).setValue(method.getType().name());
			item.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHOD_ADDRESS).setValue(method.getAddress());
			item.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHOD_CARRIER).setValue(method.getCarrier().name());
			
			list.add(method.getCarrier().getDisplayName());
		}
		
		config.getNode(StorageUtil.CONFIG_NODE_LIST).setValue(list);
		
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
	
	private List<ContactMethod> getMethods(CommentedConfigurationNode config) {
		
		List<String> list = getList(config);
		List<ContactMethod> methods = new ArrayList<ContactMethod>();
		
		for (String item: list) {
			CommentedConfigurationNode configItem = config.getNode(item);
			
			ContactMethodTypes type = ContactMethodTypes.valueOf(configItem.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHOD_TYPE).getString());
			String address = configItem.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHOD_ADDRESS).getString();
			Carriers carrier = Carriers.valueOf(configItem.getNode(StorageUtil.CONFIG_NODE_CONTACT_METHOD_CARRIER).getString());
			
			ContactMethod method = new ContactMethod(type, address);
			method.setCarrier(carrier);
			
			methods.add(method);
		}
		
		return methods;
		
	}
	
	private List<UUID> getBlacklist(CommentedConfigurationNode config) {
		
		List<UUID> blacklist = new ArrayList<UUID>();
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) config.getValue();
		
		if (list != null && !list.isEmpty()) {
			for (String uniqueId: list) {
				blacklist.add(UUID.fromString(uniqueId));
			}
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

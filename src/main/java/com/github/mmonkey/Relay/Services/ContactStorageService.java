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

public class ContactStorageService extends StorageService {
	
	public static final String CONTACT_ACCEPT_TERMS = "acceptTerms";
	public static final String CONTACT_METHODS = "methods";
	public static final String CONTACT_BLACKLIST = "blacklist";
	
	public static final String CONTACT_METHOD_TYPE = "type";
	public static final String CONTACT_METHOD_ADDRESS = "address";
	public static final String CONTACT_METHOD_CARRIER = "carrier";
	public static final String CONTACT_METHOD_ACTIVATION_KEY = "activationKey";
	public static final String CONTACT_METHOD_IS_ACTIVATED = "isActivated";

	public ContactStorageService(Relay plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, "contacts.conf"));
	}
	
	private void saveContactMethods(CommentedConfigurationNode config, Contact contact) {
		
		List<String> list = new ArrayList<String>();
		
		for (ContactMethod method: contact.getMethods()) {
			String name = getMethodName(list, method);
			CommentedConfigurationNode item = config.getNode(name);
			
			item.getNode(CONTACT_METHOD_TYPE).setValue(method.getType().name());
			item.getNode(CONTACT_METHOD_ADDRESS).setValue(method.getAddress());
			item.getNode(CONTACT_METHOD_CARRIER).setValue(method.getCarrier().name());
			item.getNode(CONTACT_METHOD_ACTIVATION_KEY).setValue(method.getActivationKey());
			item.getNode(CONTACT_METHOD_IS_ACTIVATED).setValue(method.isActivated());
			
			list.add(name);
		}
		
		config.getNode(LIST).setValue(list);
		
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
			getConfig().getNode(LIST).setValue(list);
		}
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		config.getNode(CONTACT_ACCEPT_TERMS).setValue(contact.acceptTerms());
		
		CommentedConfigurationNode methods = config.getNode(CONTACT_METHODS);
		saveContactMethods(methods, contact);
		
		CommentedConfigurationNode blacklist = config.getNode(CONTACT_BLACKLIST);
		saveBlacklist(blacklist, contact);
		
		saveConfig();
	
	}
	
	public List<String> getContactList() {
		return getList(getConfig());
	}
	
	private List<ContactMethod> getMethods(CommentedConfigurationNode config) {
		
		List<String> list = getList(config);
		List<ContactMethod> methods = new ArrayList<ContactMethod>();
		
		for (String item: list) {
			CommentedConfigurationNode configItem = config.getNode(item);
			
			ContactMethodTypes type = ContactMethodTypes.valueOf(configItem.getNode(CONTACT_METHOD_TYPE).getString());
			String address = configItem.getNode(CONTACT_METHOD_ADDRESS).getString();
			Carriers carrier = Carriers.valueOf(configItem.getNode(CONTACT_METHOD_CARRIER).getString());
			String activationKey = configItem.getNode(CONTACT_METHOD_ACTIVATION_KEY).getString();
			boolean isActivated = configItem.getNode(CONTACT_METHOD_IS_ACTIVATED).getBoolean();
			
			ContactMethod method = new ContactMethod(type, address, carrier, activationKey);
			method.isActivated(isActivated);
			
			methods.add(method);
		}
		
		return methods;
		
	}
	
	public List<String> getContactMethodList(Player player) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		CommentedConfigurationNode methodConfig = config.getNode(CONTACT_METHODS);
		
		return getList(methodConfig);
		
	}
	
	public ContactMethod getContactMethod(Player player, String name) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		CommentedConfigurationNode methodConfig = config.getNode(CONTACT_METHODS);
		CommentedConfigurationNode methodNode = methodConfig.getNode(name);
		
		if (methodNode == null) {
			return null;
		}
		
		ContactMethodTypes type = ContactMethodTypes.valueOf(methodNode.getNode(CONTACT_METHOD_TYPE).getString());
		String address = methodNode.getNode(CONTACT_METHOD_ADDRESS).getString();
		Carriers carrier = Carriers.valueOf(methodNode.getNode(CONTACT_METHOD_CARRIER).getString());
		String activationKey = methodNode.getNode(CONTACT_METHOD_ACTIVATION_KEY).getString();
		boolean isActivated = methodNode.getNode(CONTACT_METHOD_IS_ACTIVATED).getBoolean();
		
		ContactMethod method = new ContactMethod(type, address, carrier, activationKey);
		method.isActivated(isActivated);
		
		return method;
		
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
		CommentedConfigurationNode methodConfig = config.getNode(CONTACT_METHODS);
		CommentedConfigurationNode blacklistConfig = config.getNode(CONTACT_BLACKLIST);
		
		boolean terms;
		
		try {
		
			terms = config.getNode(CONTACT_ACCEPT_TERMS).getBoolean();
		
		} catch (Exception e) {
			
			terms = false;
			
		}
		
		Contact contact = new Contact();
		contact.acceptTerms(terms);
		contact.setMethods(getMethods(methodConfig));
		contact.setBlacklist(getBlacklist(blacklistConfig));
	
		return contact;
		
	}
	
	private String getMethodName(List<String> list, ContactMethod method) {
		
		String type = method.getType().name();
		int count = 1;
		
		for (String item: list) {
			if (item.contains(type)) {
				count++;
			}
		}
		
		return type + count;
		
	}
	
	public boolean deleteContactMethod(Player player, String name) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		CommentedConfigurationNode methodConfig = config.getNode(CONTACT_METHODS);
		
		List<String> list = getList(methodConfig);
		
		if (list.contains(name)) {
			
			list.remove(name);
			
			methodConfig.removeChild(name);
			methodConfig.removeChild(LIST);
			methodConfig.getNode(LIST).setValue(list);
			
			saveConfig();
			return true;
		}
		
		return false;
		
	}
	
	public boolean deleteContact(Player player) {
		
		List<String> list = getList(getConfig());
		
		if (list.contains(player.getUniqueId().toString())) {
			
			list.remove(player.getUniqueId().toString());
			
			getConfig().removeChild(player.getUniqueId().toString());
			getConfig().removeChild(LIST);
			getConfig().getNode(LIST).setValue(list);
			
			saveConfig();
			return true;
		}
		
		return false;
		
	}

}

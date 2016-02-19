package com.github.mmonkey.Relay.Events;

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.Relay;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.List;

public class PlayerChangeNameListener {

    private Relay plugin;

    @Listener
    public void onJoin(ClientConnectionEvent.Join event) {

        Player player = event.getTargetEntity();
        updateUsername(player);

    }

    private void updateUsername(Player player) {

        List<String> contactIds = plugin.getContactStorageService().getContactList();

        if (contactIds.contains(player.getUniqueId().toString())) {

            Contact contact = plugin.getContactStorageService().getContact(player);

            if (!contact.getUsername().equals(player.getName()) && !contact.getUsername().equals("")) {
                contact.setUsername(player.getName());
                saveContact(contact, player);
            }

        }

    }

    private void saveContact(Contact contact, Player player) {
        plugin.getContactStorageService().saveContact(player, contact);
    }

    public PlayerChangeNameListener(Relay plugin) {
        this.plugin = plugin;
    }

}

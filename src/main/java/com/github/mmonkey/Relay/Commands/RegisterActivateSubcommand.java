package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.Contact;
import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class RegisterActivateSubcommand extends RegisterCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        String code = (args.hasAny("code")) ? ((String) args.getOne("code").get()) : "";
        Player player = (Player) src;
        Contact contact = getContact(player);

        Relay.getLogger().info("contact: " + contact.getUsername());

        if (code.equals("")) {

            player.sendMessage(
                    Text.of(TextColors.RED, "Code cannot be verified, try again.")
            );

            Relay.getLogger().info("code is blank");

            return CommandResult.success();
        }

        List<ContactMethod> methods = contact.getMethods();

        for (ContactMethod method : methods) {

            Relay.getLogger().info("Method: " + method.getActivationKey());

            if (method.getActivationKey().equals(code)) {

                method.isActivated(true);
                method.setActivationKey("");

                contact.setMethods(methods);
                saveContact(contact, player);

                player.sendMessage(
                        Text.of(TextColors.GREEN, "Your account has been activated!")
                );

                return CommandResult.success();
            }
        }

        player.sendMessage(
                Text.of(TextColors.RED, "Code cannot be verified, try again.")
        );

        return CommandResult.success();

    }

    public RegisterActivateSubcommand(Relay plugin) {
        super(plugin);
    }

}

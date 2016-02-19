package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.Email.EmailMessage;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Services.HTMLTemplatingService;
import com.github.mmonkey.Relay.Services.MessageRelayResult;
import com.github.mmonkey.Relay.Services.MessageRelayService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;

public class RelaySendSubcommand extends RelayCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String message = (args.hasAny("message")) ? ((String) args.getOne("message").get()) : "";

        @SuppressWarnings("unchecked")
        Collection<String> players = (Collection<String>) ((args.hasAny("player")) ? args.getAll("player") : new ArrayList<String>());
        @SuppressWarnings("unchecked")
        Collection<String> templates = (Collection<String>) ((args.hasAny("template")) ? args.getAll("template") : new ArrayList<String>());

        HTMLTemplatingService templateService = new HTMLTemplatingService();
        templateService.setTemplateDirectory(plugin.getTemplateDir());

        EmailMessage email = getSendEmail(src, message);
        String emailMessage = templateService.parse(getTemplate(templates), email);

        MessageRelayService<String> service = new MessageRelayService<String>(plugin);
        MessageRelayResult result;

        if (src instanceof Player) {

            result = service.sendMessage(src.getName(), players, message, emailMessage);

        } else {

            result = service.sendMessage(players, message, emailMessage);

        }

        if (result.isSent()) {

            src.sendMessage(Text.of(TextColors.GREEN, "Message(s) succesfully sent!"));

        } else {

            if (src instanceof Player) {
                Relay.getLogger().info("Error sending message: Player " + src.getName() + " Error: " + result.getResult());
            }

            src.sendMessage(Text.of(TextColors.GOLD + "Error sending message. Details: ", TextColors.RED, result.getResult()));

        }

        return CommandResult.success();

    }

    public RelaySendSubcommand(Relay plugin) {
        super(plugin);
    }

}

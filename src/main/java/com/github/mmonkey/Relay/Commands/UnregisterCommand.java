package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class UnregisterCommand implements CommandExecutor {

    private Relay plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean delete = (args.hasAny("d")) ? (Boolean) args.getOne("d").get() : false;
        boolean cancel = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
        String contactMethod = (args.hasAny("contactMethod")) ? ((String) args.getOne("contactMethod").get()) : "";

        Player player = (Player) src;

        if (cancel && !contactMethod.equals("")) {

            player.sendMessage(
                    Text.of(Text.NEW_LINE, TextColors.GREEN, "Contact method ", TextColors.GOLD, contactMethod, TextColors.GREEN, " was not deleted.")
            );

            return CommandResult.success();

        }

        if (cancel && contactMethod.equals("")) {

            player.sendMessage(
                    Text.of(Text.NEW_LINE, TextColors.GREEN, "You account was not deleted.")
            );

            return CommandResult.success();

        }

        if (delete && !contactMethod.equals("")) {

            ContactMethod method = plugin.getContactStorageService().getContactMethod(player, contactMethod);

            if (method != null) {

                plugin.getContactStorageService().deleteContactMethod(player, method.getAddress());

                player.sendMessage(
                        Text.of(FormatUtil.empty(), TextColors.GREEN, "Contact method ", TextColors.GOLD, contactMethod, TextColors.GREEN, " has been deleted.")
                );

            } else {

                player.sendMessage(
                        Text.of(FormatUtil.empty(), TextColors.GOLD, "Contact method ", TextColors.RED, contactMethod, TextColors.GOLD, " was not found.")
                );

            }

            return CommandResult.success();

        }

        if (delete && contactMethod.equals("")) {

            plugin.getContactStorageService().deleteContact(player);

            player.sendMessage(
                    Text.of(FormatUtil.empty(), TextColors.GREEN, "All contact information has been deleted.")
            );

            return CommandResult.success();

        }

        if (!delete && !cancel && !contactMethod.equals("")) {

            Text.Builder message = Text.builder();

            message.append(Text.NEW_LINE);
            message.append(Text.of(TextColors.WHITE, "Would you like to delete method ", TextColors.GOLD, contactMethod, TextColors.WHITE, "?"));
            message.append(Text.NEW_LINE);
            message.append(getConfirmDeleteContactMethodAction(contactMethod), Text.of(" "), getCancelDeleteContactMethodAction(contactMethod));
            message.append(Text.NEW_LINE);

            player.sendMessage(message.build());

            return CommandResult.success();

        }

        if (!delete && !cancel && contactMethod.equals("")) {

            Text.Builder message = Text.builder();

            message.append(Text.NEW_LINE);
            message.append(Text.of(TextColors.WHITE, "Would you like to unregister your account?"));
            message.append(Text.NEW_LINE);
            message.append(getConfirmDeleteAccountAction(), Text.of(" "), getCancelDeleteAccountAction());
            message.append(Text.NEW_LINE);

            player.sendMessage(message.build());

            return CommandResult.success();

        }

        return CommandResult.empty();

    }

    private Text getConfirmDeleteContactMethodAction(String contactMethod) {

        return Text.builder("Yes, delete method " + contactMethod + ".")
                .onClick(TextActions.runCommand("/unregister -d " + contactMethod))
                .color(TextColors.GREEN)
                .style(TextStyles.UNDERLINE)
                .build();

    }

    private Text getCancelDeleteContactMethodAction(String contactMethod) {

        return Text.builder("No, keep method " + contactMethod + ".")
                .onClick(TextActions.runCommand("/unregister -c " + contactMethod))
                .color(TextColors.RED)
                .style(TextStyles.UNDERLINE)
                .build();

    }

    private Text getConfirmDeleteAccountAction() {

        return Text.builder("Yes, delete my account.")
                .onClick(TextActions.runCommand("/unregister -d"))
                .color(TextColors.GREEN)
                .style(TextStyles.UNDERLINE)
                .build();

    }

    private Text getCancelDeleteAccountAction() {

        return Text.builder("No, keep my account.")
                .onClick(TextActions.runCommand("/unregister -c"))
                .color(TextColors.RED)
                .style(TextStyles.UNDERLINE)
                .build();

    }

    public UnregisterCommand(Relay plugin) {
        this.plugin = plugin;
    }

}

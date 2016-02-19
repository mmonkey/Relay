package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.ContactMethod;
import com.github.mmonkey.Relay.ContactMethodTypes;
import com.github.mmonkey.Relay.Pagination.PaginatedList;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class RelayAccountSubcommand extends RelayCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        int page = (args.hasAny("page")) ? ((Integer) args.getOne("page").get()) : 1;

        Player player = (Player) src;
        EncryptionUtil encryptionUtil = getEncryptionUtil();
        PaginatedList pagination = new PaginatedList("/relay account");

        Text.Builder message = Text.builder();
        Text.Builder header = Text.builder();

        List<String> list = plugin.getContactStorageService().getContactMethodList(player);

        if (list == null || list.isEmpty()) {

            player.sendMessage(
                    Text.of(TextColors.WHITE, "You do not have any contact methods registered, to register your contact information use command:",
                            Text.NEW_LINE, TextColors.GOLD, "/register email <address>", Text.NEW_LINE,
                            TextColors.WHITE, "or ", TextColors.GOLD, "/register phone <number>")
            );

            return CommandResult.success();

        }

        for (String name : list) {

            ContactMethod method = plugin.getContactStorageService().getContactMethod(player, name);

            if (method != null) {

                try {

                    String type = (method.getType().equals(ContactMethodTypes.EMAIL)) ? "e" : "p";
                    String address = encryptionUtil.decrypt(method.getAddress());

                    Text.Builder row = Text.builder();
                    row.append(Text.of(TextColors.WHITE, address + " - "));
                    row.append(getEditAction(address, type, name));

                    pagination.add(row.build());

                } catch (Exception e) {
                    // Don't panic.
                }

            }

        }

        header.append(Text.of(TextColors.GREEN, "-------"));
        header.append(Text.of(TextColors.GREEN, " Showing contact methods page " + page + " of " + pagination.getTotalPages() + " "));
        header.append(Text.of(TextColors.GREEN, "-------"));

        pagination.setHeader(header.build());

        //TODO create footer with command info

        message.append(FormatUtil.empty());
        message.append(pagination.getPage(page));

        player.sendMessage(message.build());

        return CommandResult.success();

    }

    private Text getEditAction(String displayName, String type, String name) {

        return Text.builder("edit")
                .onClick(TextActions.runCommand("/relay edit -" + type + " " + name))
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Edit ", TextColors.GOLD, displayName)))
                .color(TextColors.DARK_AQUA)
                .build();

    }

    public RelayAccountSubcommand(Relay plugin) {
        super(plugin);
    }

}

package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.Carriers;
import com.github.mmonkey.Relay.Pagination.PaginatedList;
import com.github.mmonkey.Relay.Relay;
import com.github.mmonkey.Relay.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class RelayCarriersSubcommand extends RelayCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean select = (args.hasAny("s")) ? (Boolean) args.getOne("s").get() : false;
        boolean update = (args.hasAny("u")) ? (Boolean) args.getOne("u").get() : false;
        int page = (args.hasAny("page")) ? ((Integer) args.getOne("page").get()) : 1;
        String phone = (args.hasAny("phone")) ? ((String) args.getOne("phone").get()) : "";

        Player player = (Player) src;

        PaginatedList pagination = new PaginatedList("/relay carriers");

        if (select && !phone.equals("")) {
            pagination.setCommandSuffix("-s " + phone);
        }

        if (update && !phone.equals("")) {
            pagination.setCommandSuffix("-u " + phone);
        }

        if ((select && !phone.equals("")) || (update && !phone.equals(""))) {
            pagination.setItemsPerPage(5);
        }

        Text.Builder message = Text.builder();
        Text.Builder header = Text.builder();

        for (Carriers carrier : Carriers.values()) {

            if (!carrier.equals(Carriers.EMAIL)) {

                if (select && !phone.equals("")) {

                    pagination.add(Text.of(getSelectCarrierAction(carrier.getDisplayName(), phone, carrier.name())));

                } else if (update && !phone.equals("")) {

                    pagination.add(Text.of(getUpdateCarrierAction(carrier.getDisplayName(), phone, carrier.name())));

                } else {

                    pagination.add(Text.of(carrier.getDisplayName()));

                }
            }

        }

        header.append(Text.of(TextColors.GREEN, "-----------"));
        header.append(Text.of(TextColors.GREEN, " Showing carriers page " + page + " of " + pagination.getTotalPages() + " "));
        header.append(Text.of(TextColors.GREEN, "-----------"));

        pagination.setHeader(header.build());

        if (select && !phone.equals("")) {

            pagination.setFooter(Text.of(TextColors.GRAY, "Click on your phone carrier, or use message:",
                    Text.NEW_LINE, TextColors.GOLD, "/register phone -a " + phone + " CARRIER_NAME"));

        } else if (update && !phone.equals("")) {

            pagination.setFooter(Text.of(TextColors.GRAY, "Click on carrier to update, or use message:",
                    Text.NEW_LINE, TextColors.GOLD, "/relay edit -p " + phone + " -c CARRIER_NAME"));

        }

        message.append(FormatUtil.empty());
        message.append(pagination.getPage(page));

        player.sendMessage(message.build());

        return CommandResult.success();

    }

    private Text getSelectCarrierAction(String displayName, String phone, String carrier) {

        return Text.builder(displayName)
                .onClick(TextActions.runCommand("/register phone -a " + phone + " " + carrier))
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Select ", TextColors.GOLD, displayName, TextColors.WHITE, " as my carrier.")))
                .color(TextColors.DARK_AQUA)
                .build();

    }

    private Text getUpdateCarrierAction(String displayName, String phone, String carrier) {

        return Text.builder(displayName)
                .onClick(TextActions.runCommand("/relay edit -p " + phone + " -c " + carrier))
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Select ", TextColors.GOLD, displayName, TextColors.WHITE, " as my carrier.")))
                .color(TextColors.DARK_AQUA)
                .build();

    }

    public RelayCarriersSubcommand(Relay plugin) {
        super(plugin);
    }

}

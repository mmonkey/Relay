package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.*;
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
import org.spongepowered.api.text.format.TextStyles;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class RelayEditSubcommand extends RelayCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean email = (args.hasAny("e")) ? (Boolean) args.getOne("e").get() : false;
        boolean phone = (args.hasAny("p")) ? (Boolean) args.getOne("p").get() : false;
        boolean resend = (args.hasAny("r")) ? (Boolean) args.getOne("r").get() : false;
        String contactMethod = (args.hasAny("contactMethod")) ? ((String) args.getOne("contactMethod").get()) : "";
        String carrier = (args.hasAny("carrier")) ? ((String) args.getOne("carrier").get()).toUpperCase() : "";

        Player player = (Player) src;
        EncryptionUtil encryptionUtil = getEncryptionUtil();

        ContactMethod method = plugin.getContactStorageService().getContactMethod(player, contactMethod);
        Contact contact = getContact(player);

        if (method == null) {

            player.sendMessage(
                    Text.of(TextColors.RED, "Contact method ", TextColors.GOLD, contactMethod, TextColors.RED, " not found.")
            );

            return CommandResult.success();

        }

        if (phone && !carrier.equals("") && method.getType().equals(ContactMethodTypes.SMS)) {

            try {

                for (ContactMethod m : contact.getMethods()) {

                    if (m.getType().equals(ContactMethodTypes.SMS) && encryptionUtil.decrypt(m.getAddress()).equals(contactMethod)) {
                        m.setCarrier(Carriers.valueOf(carrier.toUpperCase()));
                        this.saveContact(contact, player);
                    }

                }


            } catch (IllegalArgumentException e) {
                // TODO Invalid carrier
            } catch (UnsupportedEncodingException e) {
                // ignore
            } catch (GeneralSecurityException e) {
                //ignore

            }

            return CommandResult.success();

        }

        if (resend) {

            this.sendActivationMessage(contact, method, player);
            return CommandResult.success();

        }

        Text.Builder message = Text.builder();

        message.append(FormatUtil.empty());
        message.append(Text.of(TextColors.GREEN, "---------------"));
        message.append(Text.of(TextColors.GREEN, " Editing Contact Method "));
        message.append(Text.of(TextColors.GREEN, "---------------"));
        message.append(Text.NEW_LINE);
        message.append(Text.NEW_LINE);

        try {

            if (email) {
                message.append(Text.of(TextColors.WHITE, "Email: ", TextColors.GOLD, encryptionUtil.decrypt(method.getAddress())));
                message.append(Text.NEW_LINE);
            }

            if (phone) {
                message.append(Text.of(TextColors.WHITE, "Phone: ", TextColors.GOLD, encryptionUtil.decrypt(method.getAddress())));
                message.append(Text.NEW_LINE);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        if (phone) {
            message.append(Text.of(TextColors.WHITE, "Carrier: ", TextColors.GOLD, method.getCarrier().getDisplayName(), TextColors.WHITE, Text.of(" - ")));
            message.append(getChangeCarrierAction(contactMethod));
            message.append(Text.NEW_LINE);
        }

        message.append(Text.of(TextColors.WHITE, "Status: "));
        message.append(getMethodStatus(method, contactMethod));
        message.append(Text.NEW_LINE);
        message.append(Text.NEW_LINE);

        message.append(Text.of("                    "));
        message.append(getDeleteMethodAction(contactMethod));
        message.append(Text.NEW_LINE);

        message.append(Text.of(TextColors.GRAY, "---------------------------------------------------"));
        message.append(Text.NEW_LINE);

        //TODO show commands.

        player.sendMessage(message.build());

        return CommandResult.success();

    }

    private Text getMethodStatus(ContactMethod method, String contactMethod) {

        Text resendActivation = Text.builder("Resend Activation")
                .onClick(TextActions.runCommand("/relay edit -r " + contactMethod))
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Resent activation message.")))
                .color(TextColors.DARK_AQUA)
                .build();

        Text.Builder methodStatus = Text.builder();

        if (!method.isActivated()) {

            methodStatus.append(Text.of(TextColors.RED, "Not Activated", TextColors.WHITE, " - "));
            methodStatus.append(resendActivation);

        } else {

            methodStatus.append(Text.of(TextColors.GREEN, "Activated"));

        }

        return methodStatus.build();

    }

    private Text getDeleteMethodAction(String contactMethod) {

        return Text.builder("Delete this Contact Method")
                .onClick(TextActions.runCommand("/unregister -d " + contactMethod))
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Delete contact method ", TextColors.GOLD, contactMethod, TextColors.WHITE, ".")))
                .color(TextColors.RED)
                .style(TextStyles.UNDERLINE)
                .build();

    }

    private Text getChangeCarrierAction(String phone) {

        return Text.builder("Change Carrier")
                .onClick(TextActions.runCommand("/relay carriers -u " + phone))
                .color(TextColors.DARK_AQUA)
                .onHover(TextActions.showText(Text.of(TextColors.WHITE, "Select a new carrier.")))
                .build();

    }

    public RelayEditSubcommand(Relay plugin) {
        super(plugin);
    }

}

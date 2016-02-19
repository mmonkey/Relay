package com.github.mmonkey.Relay.Commands;

import com.github.mmonkey.Relay.Relay;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

public class RegisterCommand extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }

    public RegisterCommand(Relay plugin) {
        super(plugin);
    }

}

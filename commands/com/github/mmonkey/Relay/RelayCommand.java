package com.github.mmonkey.Relay;

import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.Relay;

public class RelayCommand extends BaseCommand {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		return CommandResult.empty();
	}
	
	protected RelayCommand(Relay plugin) {
		super(plugin);
	}

}
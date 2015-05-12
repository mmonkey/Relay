package com.github.mmonkey.Relay.Commands;

import java.util.ArrayList;
import java.util.Collection;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.Relay;

public class RelaySendSubcommand extends RelayCommand {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean all = (args.hasAny("a")) ? (Boolean) args.getOne("a").get() : false;
		String message = (args.hasAny("message")) ? ((String) args.getOne("message").get()) : "";
		
		Collection<String> players = (args.hasAny("player")) ? args.getAll("player") : new ArrayList<String>();
		Collection<String> templates = (args.hasAny("template")) ? args.getAll("template") : new ArrayList<String>();
	
		return CommandResult.success();
		
	}
	
	public RelaySendSubcommand(Relay plugin) {
		super(plugin);
	}

}

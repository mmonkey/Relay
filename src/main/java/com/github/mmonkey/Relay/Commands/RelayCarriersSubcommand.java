package com.github.mmonkey.Relay.Commands;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandMessageFormatting;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.github.mmonkey.Relay.Utilities.FormatUtil;
import com.github.mmonkey.Relay.Carriers;
import com.github.mmonkey.Relay.PaginatedList;
import com.github.mmonkey.Relay.Relay;

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
		
		TextBuilder message = Texts.builder();
		TextBuilder header = Texts.builder();
		
		for(Carriers carrier: Carriers.values()) {
			
			if (!carrier.equals(Carriers.EMAIL)) {
				
				if (select && !phone.equals("")) {
					
					pagination.add(Texts.of(getSelectCarrierAction(carrier.getDisplayName(), phone, carrier.name())));
				
				} else if (update && !phone.equals("")) {
				
					pagination.add(Texts.of(getUpdateCarrierAction(carrier.getDisplayName(), phone, carrier.name())));
				
				} else {
					
					pagination.add(Texts.of(carrier.getDisplayName()));
					
				}
			}
			
		}
		
		header.append(Texts.of(TextColors.GREEN, "----------"));
		header.append(Texts.of(TextColors.GREEN, " Showing carriers page " + page + " of " + pagination.getTotalPages() + " "));
		header.append(Texts.of(TextColors.GREEN, "----------"));

		pagination.setHeader(header.build());
		
		if (select && !phone.equals("")) {
			
			pagination.setFooter(Texts.of(TextColors.GRAY, "Click on your phone carrier, or use message:",
				CommandMessageFormatting.NEWLINE_TEXT, TextColors.GOLD, "/register phone -a " + phone + " CARRIER_NAME"));
		
		} else if (update && !phone.equals("")) {
			
			pagination.setFooter(Texts.of(TextColors.GRAY, "Click on carrier to update, or use message:",
				CommandMessageFormatting.NEWLINE_TEXT, TextColors.GOLD, "/relay edit -p " + phone + " -c CARRIER_NAME"));
			
		}
		
		message.append(FormatUtil.empty());
		message.append(pagination.getPage(page));
		
		player.sendMessage(message.build());
		
		return CommandResult.success();
	
	}
	
	private Text getSelectCarrierAction(String displayName, String phone, String carrier) {
		
		return Texts.builder(displayName)
			.onClick(TextActions.runCommand("/register phone -a " + phone + " " + carrier))
			.onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Select ", TextColors.GOLD, displayName, TextColors.WHITE, " as my carrier.")))
			.color(TextColors.DARK_AQUA)
			.build();
		
	}
	
	private Text getUpdateCarrierAction(String displayName, String phone, String carrier) {
		
		return Texts.builder(displayName)
			.onClick(TextActions.runCommand("/relay edit -p " + phone + " -c " + carrier))
			.onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Select ", TextColors.GOLD, displayName, TextColors.WHITE, " as my carrier.")))
			.color(TextColors.DARK_AQUA)
			.build();
		
	}
	
	public RelayCarriersSubcommand(Relay plugin) {
		super(plugin);
	}

}

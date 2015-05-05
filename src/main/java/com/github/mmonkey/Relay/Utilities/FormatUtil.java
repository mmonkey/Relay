package com.github.mmonkey.Relay.Utilities;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandMessageFormatting;

public class FormatUtil {

	public static Text empty() {
		
		TextBuilder text = Texts.builder();
		for (int i = 0; i < 20; i++) {
			text.append(CommandMessageFormatting.NEWLINE_TEXT);
		}
		
		return text.build();
		
	}
	
}

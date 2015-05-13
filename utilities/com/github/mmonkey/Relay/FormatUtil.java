package com.github.mmonkey.Relay;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandMessageFormatting;

public class FormatUtil {

	public static Text empty(int numLines) {
		
		TextBuilder text = Texts.builder();
		for (int i = 0; i < numLines; i++) {
			text.append(CommandMessageFormatting.NEWLINE_TEXT);
		}
		
		return text.build();
		
	}

	public static Text empty() {
		
		return empty(20);
		
	}
	
}

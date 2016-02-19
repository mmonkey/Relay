package com.github.mmonkey.Relay.Utilities;

import org.spongepowered.api.text.Text;

public class FormatUtil {

    public static Text empty(int numLines) {

        Text.Builder text = Text.builder();
        for (int i = 0; i < numLines; i++) {
            text.append(Text.NEW_LINE);
        }

        return text.build();

    }

    public static Text empty() {

        return empty(20);

    }

}

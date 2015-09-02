package org.lanternpowered.server.text;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

public class PlainTextRepresentation implements TextRepresentation {

    @Override
    public String to(Text text) {
        return this.to(text, Locale.ENGLISH);
    }

    @Override
    public String to(Text text, Locale locale) {
        return LegacyTextRepresentation.to(checkNotNull(text, "text"), checkNotNull(locale, "locale"), new StringBuilder(), null).toString();
    }

    @Override
    public Text from(String input) throws TextMessageException {
        return Texts.of(input);
    }

    @Override
    public Text fromUnchecked(String input) {
        return Texts.of(input);
    }

}

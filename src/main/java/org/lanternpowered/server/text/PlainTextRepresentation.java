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

    /*
    private StringBuilder to(Text text, Locale locale, StringBuilder builder) {
        Iterator<Text> it = text.withChildren().iterator();
        while (it.hasNext()) {
            text = it.next();
            if (text instanceof Text.Literal) {
                builder.append(((Text.Literal) text).getContent());
            } else if (text instanceof Text.Selector) {
                builder.append(((Text.Selector) text).getSelector().toPlain());
            } else if (text instanceof Text.Translatable) {
                Text.Translatable text0 = (Text.Translatable) text;

                Translation translation = text0.getTranslation();
                ImmutableList<Object> args = text0.getArguments();

                builder.append(translation.get(locale, args.toArray(new Object[] {})));
            } else if (text instanceof Text.Placeholder) {
                Text.Placeholder text0 = (Text.Placeholder) text;

                Optional<Text> fallback = text0.getFallback();
                if (fallback.isPresent()) {
                    this.to(fallback.get(), locale, builder);
                } else {
                    builder.append(text0.getKey());
                }
            } else if (text instanceof Text.Score) {
                Text.Score text0 = (Text.Score) text;

                Optional<String> override = text0.getOverride();
                if (override.isPresent()) {
                    builder.append(override.get());
                } else {
                    builder.append(text0.getScore().getScore());
                }
            }
        }
        return builder;
    }
    */

    @Override
    public Text from(String input) throws TextMessageException {
        return Texts.of(input);
    }

    @Override
    public Text fromUnchecked(String input) {
        return Texts.of(input);
    }

}

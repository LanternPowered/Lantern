package org.lanternpowered.server.text.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JsonTextRepresentation implements TextRepresentation {

    private final Gson gson;

    public JsonTextRepresentation(TranslationManager translationManager) {
        this.gson = JsonTextSerializer.applyTo(new GsonBuilder(), translationManager).create();
    }

    /**
     * Gets the gson instance.
     * 
     * @return the gson
     */
    public Gson getGson() {
        return this.gson;
    }

    @Override
    public String to(Text text) {
        return this.gson.toJson(checkNotNull(text, "text"));
    }

    @Override
    public String to(Text text, Locale locale) {
        checkNotNull(locale, "locale"); // Not used, but the locale shouldn't be null anyway
        return this.gson.toJson(checkNotNull(text, "text"));
    }

    @Override
    public Text from(String input) throws TextMessageException {
        try {
            return this.gson.fromJson(checkNotNull(input, "input"), Text.class);
        } catch (JsonSyntaxException e) {
            throw new TextMessageException(e);
        }
    }

    @Override
    public Text fromUnchecked(String input) {
        try {
            return this.gson.fromJson(checkNotNull(input, "input"), Text.class);
        } catch (JsonSyntaxException e) {
            return Texts.of(input);
        }
    }
}

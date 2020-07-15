/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.text.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.lanternpowered.api.text.serializer.TextSerializer;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.SelectorText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Locale;

public final class LanternJsonTextSerializer extends DefaultCatalogType implements TextSerializer {

    private final Gson gson;

    public LanternJsonTextSerializer(ResourceKey key, TranslationManager translationManager) {
        super(key);
        this.gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Text.class, new JsonTextSerializer())
                .registerTypeHierarchyAdapter(LiteralText.class, new JsonTextLiteralSerializer())
                .registerTypeHierarchyAdapter(ScoreText.class, new JsonTextScoreSerializer())
                .registerTypeHierarchyAdapter(SelectorText.class, new JsonTextSelectorSerializer())
                .registerTypeHierarchyAdapter(TranslatableText.class, new JsonTextTranslatableSerializer(translationManager))
                .create();
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
    public String serialize(Text text) {
        return serialize(text, Locales.EN_US);
    }

    @Override
    public String serialize(Text text, Locale locale) {
        checkNotNull(locale, "locale");
        return fixJson(this.gson.toJson(text));
    }

    /**
     * The client doesn't like it when the server just sends a
     * primitive json string, so we put it as one entry in an array
     * to avoid errors.
     *
     * @param json The json
     * @return The result json
     */
    private static String fixJson(String json) {
        final char start = json.charAt(0);
        if (start == '[' || start == '{') {
            return json;
        } else {
            return '[' + json + ']';
        }
    }

    @Override
    public Text deserialize(String input) throws TextParseException {
        checkNotNull(input, "input");
        try {
            return this.gson.fromJson(input, Text.class);
        } catch (JsonSyntaxException e) {
            throw new TextParseException("Attempted to parse invalid json: " + input, e);
        }
    }
}

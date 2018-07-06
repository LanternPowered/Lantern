/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.text.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.text.LanternTextSerializer;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.SelectorText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Locale;

public final class LanternJsonTextSerializer extends DefaultCatalogType implements LanternTextSerializer {

    private final Gson gson;

    public LanternJsonTextSerializer(CatalogKey key, TranslationManager translationManager) {
        super(key);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Text.class, new JsonTextSerializer())
                .registerTypeAdapter(LiteralText.class, new JsonTextLiteralSerializer())
                .registerTypeAdapter(ScoreText.class, new JsonTextScoreSerializer())
                .registerTypeAdapter(SelectorText.class, new JsonTextSelectorSerializer())
                .registerTypeAdapter(TranslatableText.class, new JsonTextTranslatableSerializer(translationManager))
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

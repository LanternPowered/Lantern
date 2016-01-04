/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.SelectorText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;

import java.lang.reflect.Type;

public final class JsonTextSerializer extends JsonTextBaseSerializer implements JsonDeserializer<Text> {

    /**
     * Registers the json text serializers for the specified gson builder.
     * 
     * @param gsonBuilder the gson builder
     * @param translationManager the translation manager
     * @return the gson builder for chaining
     */
    public static GsonBuilder applyTo(GsonBuilder gsonBuilder, TranslationManager translationManager,
            boolean translateNonMinecraft) {
        gsonBuilder.registerTypeAdapter(Text.class, new JsonTextSerializer());
        gsonBuilder.registerTypeAdapter(LiteralText.class, new JsonTextLiteralSerializer());
        gsonBuilder.registerTypeAdapter(ScoreText.class, new JsonTextScoreSerializer());
        gsonBuilder.registerTypeAdapter(SelectorText.class, new JsonTextSelectorSerializer());
        gsonBuilder.registerTypeAdapter(TranslatableText.class, new JsonTextTranslatableSerializer(translationManager,
                translateNonMinecraft));
        return gsonBuilder;
    }

    @Override
    public Text deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return context.deserialize(json, LiteralText.class);
        }
        JsonObject json0 = json.getAsJsonObject();
        if (json0.has("text")) {
            return context.deserialize(json, LiteralText.class);
        } else if (json0.has("translate")) {
            return context.deserialize(json, TranslatableText.class);
        } else if (json0.has("score")) {
            return context.deserialize(json, ScoreText.class);
        } else if (json0.has("selector")) {
            return context.deserialize(json, SelectorText.class);
        } else {
            throw new JsonParseException("Unknown text format: " + json.toString());
        }
    }

}

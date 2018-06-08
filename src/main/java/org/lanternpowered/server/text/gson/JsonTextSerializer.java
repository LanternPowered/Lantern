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

import static org.lanternpowered.server.text.gson.TextConstants.SCORE_VALUE;
import static org.lanternpowered.server.text.gson.TextConstants.SELECTOR;
import static org.lanternpowered.server.text.gson.TextConstants.TEXT;
import static org.lanternpowered.server.text.gson.TextConstants.TRANSLATABLE;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.SelectorText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.lang.reflect.Type;

public final class JsonTextSerializer extends JsonTextBaseSerializer<Text> {

    /**
     * Gets the {@link Gson} that is used
     * to serialize {@link Text} objects.
     *
     * @return The gson
     */
    public static Gson getGson() {
        return ((LanternJsonTextSerializer) TextSerializers.JSON).getGson();
    }

    @Override
    public JsonElement serialize(Text src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof LiteralText) {
            return context.serialize(src, LiteralText.class);
        } else if (src instanceof TranslatableText) {
            return context.serialize(src, TranslatableText.class);
        } else if (src instanceof ScoreText) {
            return context.serialize(src, ScoreText.class);
        } else if (src instanceof SelectorText) {
            return context.serialize(src, SelectorText.class);
        } else {
            throw new IllegalStateException("Attempted to serialize an unsupported text type: " + src.getClass().getName());
        }
    }

    @Override
    public Text deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return context.deserialize(json, LiteralText.class);
        }
        if (json.isJsonArray()) {
            final Text.Builder builder = Text.builder();
            builder.append(context.<Text[]>deserialize(json, Text[].class));
            return builder.build();
        }
        final JsonObject obj = json.getAsJsonObject();
        if (obj.has(TEXT)) {
            return context.deserialize(json, LiteralText.class);
        } else if (obj.has(TRANSLATABLE)) {
            return context.deserialize(json, TranslatableText.class);
        } else if (obj.has(SCORE_VALUE)) {
            return context.deserialize(json, ScoreText.class);
        } else if (obj.has(SELECTOR)) {
            return context.deserialize(json, SelectorText.class);
        } else {
            throw new JsonParseException("Unknown text format: " + json.toString());
        }
    }
}

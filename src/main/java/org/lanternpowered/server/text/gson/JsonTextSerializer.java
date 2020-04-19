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

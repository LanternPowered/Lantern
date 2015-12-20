/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes/deserializes the {@link Text.Placeholder} object type introduced by sponge.
 */
public final class JsonTextPlaceholderSerializer extends JsonTextBaseSerializer implements JsonDeserializer<Text.Placeholder>, JsonSerializer<Text.Placeholder> {

    @Override
    public JsonElement serialize(Text.Placeholder src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        List<Text> children = src.getChildren();
        Optional<Text> fallback = src.getFallback();
        if (fallback.isPresent()) {
            children.add(0, fallback.get());
            json.addProperty("fallbackAsExtra", true);
        }
        this.serialize(json, src, context, children);
        json.addProperty("placeholderKey", src.getKey());
        // Make sure that if a vanilla server/client reads the format it won't throw errors
        json.addProperty("text", "");
        return json;
    }

    @Override
    public Text.Placeholder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        String key = json0.get("placeholderKey").getAsString();
        JsonArray array = json0.has("extra") ? json0.getAsJsonArray("extra") : null;
        Text fallback = null;
        if (array != null && array.size() > 0 && json0.has("fallbackAsExtra") &&
                json0.get("fallbackAsExtra").getAsBoolean()) {
            // We copy all the objects first to avoid modifying the elements
            // used by the serializer, somebody may still need them
            JsonArray array0 = new JsonArray();
            for (int i = 1 ; i < array.size(); i++) {
                array0.add(array.get(i));
            }
            fallback = context.deserialize(array.get(0), Text.class);
            array = array0;
        }
        TextBuilder.Placeholder builder = Texts.placeholderBuilder(key).fallback(fallback);
        this.deserialize(json0, builder, context, array);
        return builder.build();
    }

}

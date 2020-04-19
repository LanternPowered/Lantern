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
package org.lanternpowered.server.script.function.value.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.api.script.function.value.FloatValueProvider;

import java.lang.reflect.Type;

public class RangeFloatValueProviderJsonSerializer implements JsonSerializer<FloatValueProvider.Range>,
        JsonDeserializer<FloatValueProvider.Range> {

    @Override
    public JsonElement serialize(FloatValueProvider.Range src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        json.addProperty("min", src.getMin());
        json.addProperty("max", src.getMax());
        return json;
    }

    @Override
    public FloatValueProvider.Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = json.getAsJsonObject();
        final float min = obj.get("min").getAsFloat();
        final float max = obj.get("max").getAsFloat();
        return FloatValueProvider.range(min, max);
    }
}

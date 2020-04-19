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
import org.lanternpowered.api.script.function.value.IntValueProvider;

import java.lang.reflect.Type;

public class RangeIntValueProviderJsonSerializer implements JsonSerializer<IntValueProvider.Range>,
        JsonDeserializer<IntValueProvider.Range> {

    @Override
    public JsonElement serialize(IntValueProvider.Range src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        json.addProperty("min", src.getMin());
        json.addProperty("max", src.getMax());
        return json;
    }

    @Override
    public IntValueProvider.Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = json.getAsJsonObject();
        final int min = obj.get("min").getAsInt();
        final int max = obj.get("max").getAsInt();
        return IntValueProvider.range(min, max);
    }
}

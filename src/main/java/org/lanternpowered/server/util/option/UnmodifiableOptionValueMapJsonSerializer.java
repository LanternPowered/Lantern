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
package org.lanternpowered.server.util.option;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UnmodifiableOptionValueMapJsonSerializer implements JsonSerializer<UnmodifiableOptionValueMap>,
        JsonDeserializer<UnmodifiableOptionValueMap> {

    @Override
    public JsonElement serialize(UnmodifiableOptionValueMap src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.optionValueMap);
    }

    @Override
    public UnmodifiableOptionValueMap deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        return new UnmodifiableOptionValueMap(context.deserialize(json, SimpleOptionValueMap.class));
    }
}

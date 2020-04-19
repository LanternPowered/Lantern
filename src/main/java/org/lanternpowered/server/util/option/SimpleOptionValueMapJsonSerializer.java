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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.server.game.Lantern;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class SimpleOptionValueMapJsonSerializer implements JsonSerializer<SimpleOptionValueMap>,
        JsonDeserializer<SimpleOptionValueMap> {

    public SimpleOptionValueMapJsonSerializer() {
    }

    @Override
    public JsonElement serialize(SimpleOptionValueMap src, Type typeOfSrc,
            JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        for (Map.Entry<String, Object> entry : src.values.entrySet()) {
            json.add(entry.getKey(), context.serialize(entry.getValue()));
        }
        return json;
    }

    @Override
    public SimpleOptionValueMap deserialize(JsonElement element, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final JsonObject json = element.getAsJsonObject();
        //noinspection unchecked
        final OptionValueMap map = new SimpleOptionValueMap();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            final Optional<Option<?>> option = Option.get(entry.getKey());
            if (option.isPresent()) {
                map.put(option.get(), context.deserialize(entry.getValue(),
                        option.get().getTypeToken().getType()));
            } else {
                Lantern.getLogger().warn("Attempted to add a value for a non existing option: {}",
                        entry.getKey());
            }
        }
        return (SimpleOptionValueMap) map;
    }
}

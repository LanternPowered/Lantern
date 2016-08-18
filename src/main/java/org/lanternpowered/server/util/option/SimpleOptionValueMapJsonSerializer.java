/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

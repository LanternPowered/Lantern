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
package org.lanternpowered.server.world.weather;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.server.util.option.OptionValueMap;
import org.lanternpowered.server.util.option.SimpleOptionValueMap;
import org.lanternpowered.server.util.option.UnmodifiableOptionValueMap;

import java.lang.reflect.Type;
import java.util.Set;

public class WeatherBuilderJsonSerializer implements JsonSerializer<WeatherBuilder>, JsonDeserializer<WeatherBuilder> {

    @Override
    public JsonElement serialize(WeatherBuilder src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        if (!src.aliases.isEmpty()) {
            json.add("aliases", context.serialize(src.aliases));
        }
        json.add("options", context.serialize(src.options));
        json.add("action", context.serialize(src.action));
        json.addProperty("weight", src.weight);
        if (src.duration != WeatherBuilder.DEFAULT_DURATION) {
            json.add("duration", context.serialize(src.duration));
        }
        return json;
    }

    @Override
    public WeatherBuilder deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject json = element.getAsJsonObject();
        final Set<String> aliases;
        if (json.has("aliases")) {
            aliases = ImmutableSet.copyOf((String[]) context.deserialize(json.get("aliases"), String[].class));
        } else {
            aliases = ImmutableSet.of();
        }
        final OptionValueMap options;
        if (json.has("options")) {
            options = context.deserialize(json.get("options"), UnmodifiableOptionValueMap.class);
        } else {
            options = new UnmodifiableOptionValueMap(new SimpleOptionValueMap());
        }
        final Action action = context.deserialize(json.has("action") ? json.get("action") : JsonNull.INSTANCE, Action.class);
        final WeatherBuilder builder = LanternWeather.builder().action(action).aliases(aliases).options(options);
        if (json.has("name")) {
            builder.name(json.get("name").getAsString());
        }
        if (json.has("weight")) {
            builder.weight(json.get("weight").getAsDouble());
        }
        if (json.has("duration")) {
            builder.duration(context.deserialize(json.get("duration"), IntValueProvider.class));
        }
        return builder;
    }
}

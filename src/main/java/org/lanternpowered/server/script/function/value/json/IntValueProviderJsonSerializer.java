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
package org.lanternpowered.server.script.function.value.json;

import static org.lanternpowered.server.script.transformer.Transformer.SCRIPT_PREFIX;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.lanternpowered.server.script.ScriptFunction;

import java.lang.reflect.Type;

public class IntValueProviderJsonSerializer implements JsonSerializer<IntValueProvider>, JsonDeserializer<IntValueProvider> {

    @Override
    public JsonElement serialize(IntValueProvider src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof IntValueProvider.Constant) {
            //noinspection ConstantConditions
            return new JsonPrimitive(src.get(null));
        } else if (src instanceof IntValueProvider.Range) {
            final IntValueProvider.Range src0 = (IntValueProvider.Range) src;
            final JsonObject json = new JsonObject();
            json.addProperty("min", src0.getMin());
            json.addProperty("max", src0.getMax());
            return json;
        } else if (src instanceof ScriptFunction) {
            return new JsonPrimitive(((ScriptFunction) src).getScript().getCode());
        }
        throw new IllegalArgumentException("Unknown double value provider type: " + src.getClass().getName());
    }

    @Override
    public IntValueProvider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            final JsonObject json0 = json.getAsJsonObject();
            final int min = json0.get("min").getAsInt();
            final int max = json0.get("max").getAsInt();
            return IntValueProvider.range(min, max);
        }
        final String value = json.getAsString().trim();
        if (value.startsWith(SCRIPT_PREFIX)) {
            return LanternScriptGameRegistry.get().compile(value, IntValueProvider.class).get();
        }
        return IntValueProvider.constant(json.getAsInt());
    }
}

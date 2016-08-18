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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.lanternpowered.api.script.function.condition.AndCondition;
import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.condition.OrCondition;
import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.api.script.function.value.IntValueProviderType;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.lanternpowered.server.script.ScriptFunction;
import org.lanternpowered.server.script.json.ObjectTypeAdapterFactory;

import java.io.IOException;

public class IntValueProviderTypeAdapterFactory extends ObjectTypeAdapterFactory<IntValueProvider, IntValueProviderType> {

    public IntValueProviderTypeAdapterFactory(AbstractObjectTypeRegistryModule<IntValueProvider, IntValueProviderType> registry,
            TypeToken<IntValueProvider> type) {
        super(registry, type);
    }

    @Override
    protected JsonElement serialize(TypeToken<IntValueProvider> type, IntValueProvider value, Gson gson) throws IOException {
        if (value instanceof IntValueProvider.Range) {
            return gson.getDelegateAdapter(this, TypeToken.get(IntValueProvider.Range.class))
                    .toJsonTree((IntValueProvider.Range) value);
        } else if (value instanceof IntValueProvider.Constant) {
            return gson.getDelegateAdapter(this, TypeToken.get(IntValueProvider.Constant.class))
                    .toJsonTree((IntValueProvider.Constant) value);
        } else if (value instanceof ScriptFunction) {
            return gson.getDelegateAdapter(this, TypeToken.get(ScriptFunction.class))
                    .toJsonTree((ScriptFunction) value);
        }
        return super.serialize(type, value, gson);
    }

    @Override
    protected IntValueProvider deserialize(TypeToken<IntValueProvider> type, JsonElement element, Gson gson) {
        final Class<? super IntValueProvider> raw = type.getRawType();
        if (IntValueProvider.Constant.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(IntValueProvider.Constant.class)).fromJsonTree(element);
        } else if (IntValueProvider.Range.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(IntValueProvider.Range.class)).fromJsonTree(element);
        } else if (raw == IntValueProvider.class) {
            // The actual condition type isn't provided, we will try
            // to assume the right type based in the json format
            if (element.isJsonPrimitive()) {
                return gson.getDelegateAdapter(this, TypeToken.get(IntValueProvider.Constant.class)).fromJsonTree(element);
            }
        }
        return super.deserialize(type, element, gson);
    }
}

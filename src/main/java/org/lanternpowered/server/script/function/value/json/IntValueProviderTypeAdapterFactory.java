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

import static org.lanternpowered.server.script.transformer.Transformer.SCRIPT_PREFIX;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
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
                final String value = element.getAsString();
                if (value.startsWith(SCRIPT_PREFIX)) {
                    return LanternScriptGameRegistry.get().compile(value, IntValueProvider.class).get();
                }
                return gson.getDelegateAdapter(this, TypeToken.get(IntValueProvider.Constant.class)).fromJsonTree(element);
            }
        }
        return super.deserialize(type, element, gson);
    }
}

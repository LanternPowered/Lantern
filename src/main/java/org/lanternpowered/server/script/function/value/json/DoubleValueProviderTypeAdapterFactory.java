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
import org.lanternpowered.api.script.function.value.DoubleValueProvider;
import org.lanternpowered.api.script.function.value.DoubleValueProviderType;
import org.lanternpowered.api.script.function.value.FloatValueProvider;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.lanternpowered.server.script.ScriptFunction;
import org.lanternpowered.server.script.json.ObjectTypeAdapterFactory;

import java.io.IOException;

public class DoubleValueProviderTypeAdapterFactory extends ObjectTypeAdapterFactory<DoubleValueProvider, DoubleValueProviderType> {

    public DoubleValueProviderTypeAdapterFactory(AbstractObjectTypeRegistryModule<DoubleValueProvider, DoubleValueProviderType> registry,
            TypeToken<DoubleValueProvider> type) {
        super(registry, type);
    }

    @Override
    protected JsonElement serialize(TypeToken<DoubleValueProvider> type, DoubleValueProvider value, Gson gson) throws IOException {
        if (value instanceof DoubleValueProvider.Range) {
            return gson.getDelegateAdapter(this, TypeToken.get(DoubleValueProvider.Range.class))
                    .toJsonTree((DoubleValueProvider.Range) value);
        } else if (value instanceof DoubleValueProvider.Constant) {
            return gson.getDelegateAdapter(this, TypeToken.get(DoubleValueProvider.Constant.class))
                    .toJsonTree((DoubleValueProvider.Constant) value);
        } else if (value instanceof ScriptFunction) {
            return gson.getDelegateAdapter(this, TypeToken.get(ScriptFunction.class))
                    .toJsonTree((ScriptFunction) value);
        }
        return super.serialize(type, value, gson);
    }

    @Override
    protected DoubleValueProvider deserialize(TypeToken<DoubleValueProvider> type, JsonElement element, Gson gson) {
        final Class<? super DoubleValueProvider> raw = type.getRawType();
        if (FloatValueProvider.Constant.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(DoubleValueProvider.Constant.class)).fromJsonTree(element);
        } else if (FloatValueProvider.Range.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(DoubleValueProvider.Range.class)).fromJsonTree(element);
        } else if (raw == DoubleValueProvider.class) {
            // The actual condition type isn't provided, we will try
            // to assume the right type based in the json format
            if (element.isJsonPrimitive()) {
                final String value = element.getAsString();
                if (value.startsWith(SCRIPT_PREFIX)) {
                    return LanternScriptGameRegistry.get().compile(value, DoubleValueProvider.class).get();
                }
                return gson.getDelegateAdapter(this, TypeToken.get(DoubleValueProvider.Constant.class)).fromJsonTree(element);
            }
        }
        return super.deserialize(type, element, gson);
    }
}

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
import org.lanternpowered.api.script.function.value.FloatValueProvider;
import org.lanternpowered.api.script.function.value.FloatValueProviderType;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.lanternpowered.server.script.ScriptFunction;
import org.lanternpowered.server.script.json.ObjectTypeAdapterFactory;

import java.io.IOException;

public class FloatValueProviderTypeAdapterFactory extends ObjectTypeAdapterFactory<FloatValueProvider, FloatValueProviderType> {

    public FloatValueProviderTypeAdapterFactory(AbstractObjectTypeRegistryModule<FloatValueProvider, FloatValueProviderType> registry,
            TypeToken<FloatValueProvider> type) {
        super(registry, type);
    }

    @Override
    protected JsonElement serialize(TypeToken<FloatValueProvider> type, FloatValueProvider value, Gson gson) throws IOException {
        if (value instanceof FloatValueProvider.Range) {
            return gson.getDelegateAdapter(this, TypeToken.get(FloatValueProvider.Range.class))
                    .toJsonTree((FloatValueProvider.Range) value);
        } else if (value instanceof FloatValueProvider.Constant) {
            return gson.getDelegateAdapter(this, TypeToken.get(FloatValueProvider.Constant.class))
                    .toJsonTree((FloatValueProvider.Constant) value);
        } else if (value instanceof ScriptFunction) {
            return gson.getDelegateAdapter(this, TypeToken.get(ScriptFunction.class))
                    .toJsonTree((ScriptFunction) value);
        }
        return super.serialize(type, value, gson);
    }

    @Override
    protected FloatValueProvider deserialize(TypeToken<FloatValueProvider> type, JsonElement element, Gson gson) {
        final Class<? super FloatValueProvider> raw = type.getRawType();
        if (FloatValueProvider.Constant.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(FloatValueProvider.Constant.class)).fromJsonTree(element);
        } else if (FloatValueProvider.Range.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(FloatValueProvider.Range.class)).fromJsonTree(element);
        } else if (raw == FloatValueProvider.class) {
            // The actual condition type isn't provided, we will try
            // to assume the right type based in the json format
            if (element.isJsonPrimitive()) {
                final String value = element.getAsString();
                if (value.startsWith(SCRIPT_PREFIX)) {
                    return LanternScriptGameRegistry.get().compile(value, FloatValueProvider.class).get();
                }
                return gson.getDelegateAdapter(this, TypeToken.get(FloatValueProvider.Constant.class)).fromJsonTree(element);
            }
        }
        return super.deserialize(type, element, gson);
    }
}

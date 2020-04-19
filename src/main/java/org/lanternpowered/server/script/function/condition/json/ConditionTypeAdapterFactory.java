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
package org.lanternpowered.server.script.function.condition.json;

import static org.lanternpowered.server.script.transformer.Transformer.SCRIPT_PREFIX;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.lanternpowered.api.script.function.condition.AndCondition;
import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.condition.ConditionType;
import org.lanternpowered.api.script.function.condition.OrCondition;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.lanternpowered.server.script.ScriptFunction;
import org.lanternpowered.server.script.json.ObjectTypeAdapterFactory;

import java.io.IOException;

public class ConditionTypeAdapterFactory extends ObjectTypeAdapterFactory<Condition, ConditionType> {

    public ConditionTypeAdapterFactory(AbstractObjectTypeRegistryModule<Condition, ConditionType> registry, TypeToken<Condition> type) {
        super(registry, type);
    }

    @Override
    protected JsonElement serialize(TypeToken<Condition> type, Condition value, Gson gson) throws IOException {
        if (value instanceof AndCondition) {
            return gson.getDelegateAdapter(this, TypeToken.get(AndCondition.class)).toJsonTree((AndCondition) value);
        } else if (value instanceof OrCondition) {
            return gson.getDelegateAdapter(this, TypeToken.get(OrCondition.class)).toJsonTree((OrCondition) value);
        } else if (value instanceof ScriptFunction) {
            return gson.getDelegateAdapter(this, TypeToken.get(ScriptFunction.class)).toJsonTree((ScriptFunction) value);
        }
        return super.serialize(type, value, gson);
    }

    @Override
    protected Condition deserialize(TypeToken<Condition> type, JsonElement element, Gson gson) {
        final Class<? super Condition> raw = type.getRawType();
        if (AndCondition.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(AndCondition.class)).fromJsonTree(element);
        } else if (OrCondition.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(OrCondition.class)).fromJsonTree(element);
        } else if (raw == Condition.class) {
            // The actual condition type isn't provided, we will try
            // to assume the right type based in the json format
            if (element.isJsonArray()) {
                return gson.getAdapter(AndCondition.class).fromJsonTree(element);
            } else if (element.isJsonPrimitive()) {
                final String value = element.getAsString();
                if (value.startsWith(SCRIPT_PREFIX)) {
                    return LanternScriptGameRegistry.get().compile(value, Condition.class).get();
                }
            }
        }
        return super.deserialize(type, element, gson);
    }
}


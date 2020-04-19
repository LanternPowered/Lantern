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
package org.lanternpowered.server.script.function.action.json;

import static org.lanternpowered.server.script.transformer.Transformer.SCRIPT_PREFIX;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.action.ActionType;
import org.lanternpowered.api.script.function.action.ConditionalAction;
import org.lanternpowered.api.script.function.action.EmptyAction;
import org.lanternpowered.api.script.function.action.MultiAction;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;
import org.lanternpowered.server.script.LanternScriptGameRegistry;
import org.lanternpowered.server.script.ScriptFunction;
import org.lanternpowered.server.script.json.ObjectTypeAdapterFactory;

import java.io.IOException;

public class ActionTypeAdapterFactory extends ObjectTypeAdapterFactory<Action, ActionType> {

    public ActionTypeAdapterFactory(AbstractObjectTypeRegistryModule<Action, ActionType> registry, TypeToken<Action> type) {
        super(registry, type);
    }

    @Override
    protected JsonElement serialize(TypeToken<Action> type, Action value, Gson gson) throws IOException {
        if (value instanceof ConditionalAction) {
            return gson.getDelegateAdapter(this, TypeToken.get(ConditionalAction.class)).toJsonTree((ConditionalAction) value);
        } else if (value instanceof EmptyAction) {
            return gson.getDelegateAdapter(this, TypeToken.get(EmptyAction.class)).toJsonTree((EmptyAction) value);
        } else if (value instanceof MultiAction) {
            return gson.getDelegateAdapter(this, TypeToken.get(MultiAction.class)).toJsonTree((MultiAction) value);
        } else if (value instanceof ScriptFunction) {
            return gson.getDelegateAdapter(this, TypeToken.get(ScriptFunction.class)).toJsonTree((ScriptFunction) value);
        }
        return super.serialize(type, value, gson);
    }

    @Override
    protected Action deserialize(TypeToken<Action> type, JsonElement element, Gson gson) {
        final Class<? super Action> raw = type.getRawType();
        if (ScriptFunction.class.isAssignableFrom(raw)) {
            return LanternScriptGameRegistry.get().compile(element.getAsString(), Action.class).get();
        } else if (ConditionalAction.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(ConditionalAction.class)).fromJsonTree(element);
        } else if (EmptyAction.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(EmptyAction.class)).fromJsonTree(element);
        } else if (MultiAction.class.isAssignableFrom(raw)) {
            return gson.getDelegateAdapter(this, TypeToken.get(MultiAction.class)).fromJsonTree(element);
        } else if (raw == Action.class) {
            // The actual action type isn't provided, we will try
            // to assume the right type based in the json format
            if (element.isJsonNull()) {
                return Action.empty();
            } else if (element.isJsonArray()) {
                return gson.getDelegateAdapter(this, TypeToken.get(MultiAction.class)).fromJsonTree(element);
            } else if (element.isJsonObject()) {
                final JsonObject obj = element.getAsJsonObject();
                if (obj.has(ConditionalActionJsonSerializer.ACTION) &&
                        obj.has(ConditionalActionJsonSerializer.CONDITION)) {
                    return gson.getDelegateAdapter(this, TypeToken.get(ConditionalAction.class)).fromJsonTree(element);
                }
            } else {
                final String value = element.getAsString();
                if (value.startsWith(SCRIPT_PREFIX)) {
                    return LanternScriptGameRegistry.get().compile(value, Action.class).get();
                }
            }
        }
        return super.deserialize(type, element, gson);
    }
}

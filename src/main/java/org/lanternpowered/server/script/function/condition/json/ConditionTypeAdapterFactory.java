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
package org.lanternpowered.server.script.function.condition.json;

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
                return LanternScriptGameRegistry.get().compile(element.getAsString(), Condition.class).get();
            }
        }
        return super.deserialize(type, element, gson);
    }
}


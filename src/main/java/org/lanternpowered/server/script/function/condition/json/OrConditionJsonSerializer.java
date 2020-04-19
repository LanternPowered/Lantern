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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.condition.OrCondition;

import java.lang.reflect.Type;

public class OrConditionJsonSerializer implements JsonSerializer<OrCondition>, JsonDeserializer<OrCondition> {

    @Override
    public JsonElement serialize(OrCondition src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getConditions());
    }

    @Override
    public OrCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Condition.or((Condition[]) context.deserialize(json, Condition[].class));
    }
}

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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.action.ConditionalAction;
import org.lanternpowered.api.script.function.condition.Condition;

import java.lang.reflect.Type;

public class ConditionalActionJsonSerializer implements JsonSerializer<ConditionalAction>, JsonDeserializer<ConditionalAction> {

    static final String ACTION = "action";
    static final String CONDITION = "condition";

    @Override
    public JsonElement serialize(ConditionalAction src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();
        json.add(CONDITION, context.serialize(src.getCondition()));
        json.add(ACTION, context.serialize(src.getAction()));
        return json;
    }

    @Override
    public ConditionalAction deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();
        final Condition condition = context.deserialize(obj.get(CONDITION), Condition.class);
        final Action action = context.deserialize(obj.get(ACTION), Action.class);
        return Action.conditional(condition, action);
    }
}

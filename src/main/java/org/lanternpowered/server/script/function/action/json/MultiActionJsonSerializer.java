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
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.action.MultiAction;

import java.lang.reflect.Type;
import java.util.List;

public class MultiActionJsonSerializer implements JsonSerializer<MultiAction>, JsonDeserializer<MultiAction> {

    @Override
    public JsonElement serialize(MultiAction src, Type typeOfSrc, JsonSerializationContext context) {
        final List<Action> actions = src.getActions();
        return context.serialize(actions.size() == 1 ? actions.get(0) : actions);
    }

    @Override
    public MultiAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Action[] actions;
        if (!json.isJsonArray()) {
            actions = new Action[] { context.deserialize(json, Action.class) };
        } else {
            actions = context.deserialize(json, Action[].class);
        }
        return Action.multi(actions);
    }
}

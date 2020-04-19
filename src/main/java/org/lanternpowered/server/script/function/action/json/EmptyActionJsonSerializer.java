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
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.action.EmptyAction;

import java.lang.reflect.Type;

public class EmptyActionJsonSerializer implements JsonSerializer<EmptyAction>, JsonDeserializer<EmptyAction> {

    @Override
    public JsonElement serialize(EmptyAction src, Type typeOfSrc, JsonSerializationContext context) {
        return JsonNull.INSTANCE;
    }

    @Override
    public EmptyAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Action.empty();
    }
}

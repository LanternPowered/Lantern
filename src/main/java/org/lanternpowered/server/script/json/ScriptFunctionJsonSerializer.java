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
package org.lanternpowered.server.script.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.server.script.ScriptFunction;

import java.lang.reflect.Type;

public class ScriptFunctionJsonSerializer implements JsonSerializer<ScriptFunction> {

    @Override
    public JsonElement serialize(ScriptFunction src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getScript().getCode());
    }
}

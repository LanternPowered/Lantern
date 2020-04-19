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
import org.lanternpowered.api.script.Script;
import org.lanternpowered.server.script.LanternScript;

import java.lang.reflect.Type;

public class ScriptJsonSerializer implements JsonSerializer<Script> {

    @Override
    public JsonElement serialize(Script src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(((LanternScript) src).getCode());
    }
}

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
package org.lanternpowered.server.util;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public final class JsonResourceBundle extends ResourceBundle {

    private static final Gson GSON = new Gson();

    public static JsonResourceBundle loadFrom(InputStream is) {
        return new JsonResourceBundle(GSON.fromJson(new InputStreamReader(is), JsonObject.class));
    }

    private final Map<String, String> lookup;

    public JsonResourceBundle(JsonObject jsonObject) {
        this.lookup = new HashMap<>();
        jsonObject.entrySet().forEach(e -> this.lookup.put(e.getKey(), e.getValue().getAsString()));
    }

    @Override
    protected Object handleGetObject(String key) {
        requireNonNull(key);
        return this.lookup.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(this.lookup.keySet());
    }
}

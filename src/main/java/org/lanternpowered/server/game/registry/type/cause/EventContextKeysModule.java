/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.game.registry.type.cause;

import com.google.common.base.Joiner;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.event.LanternEventContextKey;
import org.lanternpowered.server.event.LanternEventContextKeys;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class EventContextKeysModule extends AdditionalPluginCatalogRegistryModule<EventContextKey> {

    private static final EventContextKeysModule instance = new EventContextKeysModule();

    /**
     * Gets the {@link EventContextKeysModule}.
     *
     * @return The event context keys registry module
     */
    public static EventContextKeysModule get() {
        return instance;
    }

    private EventContextKeysModule() {
        super(EventContextKeys.class, LanternEventContextKeys.class, ContextKeys.class);
    }

    @Override
    public void registerDefaults() {
        // Sponge
        registerKeysFor("sponge", EventContextKeys.class);

        // Lantern
        registerKeysFor("lantern", LanternEventContextKeys.class);
        registerKeysFor("lantern", ContextKeys.class);
    }

    private void registerKeysFor(String pluginId, Class<?> catalogClass) {
        final TypeVariable<?> typeVariable = EventContextKey.class.getTypeParameters()[0];
        // Sponge
        for (Field field : catalogClass.getFields()) {
            // Skip fields that aren't event context keys
            if (!EventContextKey.class.isAssignableFrom(field.getType())) {
                return;
            }
            // Extract the generic type from the field signature
            final TypeToken<?> typeToken = TypeToken.of(field.getGenericType()).resolveType(typeVariable);
            // Get the plugin id, and make a nicely formatted name
            final String id = field.getName().toLowerCase();
            final String name = formatName(id);
            // Register the key
            register(new LanternEventContextKey<>(pluginId, id, name, typeToken));
        }
    }

    private static String formatName(String name) {
        return Joiner.on(' ').join(Arrays.stream(name.split("_"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.toList()));
    }
}

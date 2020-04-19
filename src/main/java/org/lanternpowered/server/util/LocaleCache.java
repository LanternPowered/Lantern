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

import org.spongepowered.api.text.translation.locale.Locales;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LocaleCache {

    private static final Map<String, Locale> cache = new HashMap<>();

    static {
        for (Field field : Locales.class.getFields()) {
            final String name = field.getName();
            if (name.indexOf('_') < 0) {
                continue;
            }
            try {
                cache.put(name.toLowerCase(Locale.ENGLISH), (Locale) field.get(null));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Gets the {@link Locale} for the given name.
     *
     * @param name The name
     * @return The locale
     */
    public static Locale get(String name) {
        return cache.computeIfAbsent(name.toLowerCase(Locale.ENGLISH), name1 -> {
            final String[] parts = name1.split("_", 3);
            final Locale locale;
            if (parts.length == 3) {
                locale = new Locale(parts[0].toLowerCase(), parts[1].toUpperCase(), parts[2]);
            } else if (parts.length == 2) {
                locale = new Locale(parts[0].toLowerCase(), parts[1].toUpperCase());
            } else {
                locale = new Locale(parts[0]);
            }
            return locale;
        });
    }

    private LocaleCache() {
    }
}

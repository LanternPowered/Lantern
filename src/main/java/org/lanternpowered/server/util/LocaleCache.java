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

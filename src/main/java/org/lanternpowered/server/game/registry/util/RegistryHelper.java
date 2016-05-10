/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.game.registry.util;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.ReflectionHelper;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

@NonnullByDefault
public final class RegistryHelper {

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping) {
        return mapFields(apiClass, mapping, null);
    }

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping, @Nullable Set<String> ignoredFields) {
        return mapFields(apiClass, fieldName -> mapping.get(fieldName.toLowerCase(Locale.ENGLISH)), ignoredFields, false);
    }

    public static boolean mapFieldsIgnoreWarning(Class<?> apiClass, Map<String, ?> mapping) {
        return mapFields(apiClass, fieldname -> mapping.get(fieldname.toLowerCase(Locale.ENGLISH)), null, true);
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction) {
        return mapFields(apiClass, mapFunction, null, false);
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction, @Nullable Set<String> ignoredFields) {
        return mapFields(apiClass, mapFunction, ignoredFields, false);
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction, @Nullable Set<String> ignoredFields, boolean ignore) {
        boolean mappingSuccess = true;
        for (Field f : apiClass.getDeclaredFields()) {
            if (ignoredFields != null && ignoredFields.contains(f.getName())) {
                continue;
            }
            try {
                Object value = mapFunction.apply(f.getName());
                if (value == null && !ignore) {
                    // Lantern.getLogger().warn("Skipping {}.{}", f.getDeclaringClass().getName(), f.getName());
                    continue;
                }
                ReflectionHelper.setField(f, null, value);
            } catch (Exception e) {
                if (!ignore) {
                    Lantern.getLogger().error("Error while mapping {}.{}", f.getDeclaringClass().getName(), f.getName(), e);
                }
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean setFactory(Class<?> apiClass, Object factory) {
        try {
            ReflectionHelper.setField(apiClass.getDeclaredField("factory"), null, factory);
            return true;
        } catch (Exception e) {
            Lantern.getLogger().error("Error while setting factory on {}", apiClass, e);
            return false;
        }
    }

    private RegistryHelper() {
    }

}

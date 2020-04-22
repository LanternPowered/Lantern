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
package org.lanternpowered.server.game.registry.util;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

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
            } catch (Throwable e) {
                Lantern.getLogger().error("Error while mapping {}.{}", f.getDeclaringClass().getName(), f.getName(), e);
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean setFactory(Class<?> apiClass, Object factory) {
        try {
            ReflectionHelper.setField(apiClass.getDeclaredField("factory"), null, factory);
            return true;
        } catch (Throwable e) {
            Lantern.getLogger().error("Error while setting factory on {}", apiClass, e);
            return false;
        }
    }

    private RegistryHelper() {
    }

}

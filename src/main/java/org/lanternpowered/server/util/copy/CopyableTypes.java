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
package org.lanternpowered.server.util.copy;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unchecked")
final class CopyableTypes {

    private static final Map<TypeToken<?>, Function> copyFunctions = new HashMap<>();
    private static final LoadingCache<TypeToken<?>, Optional<Function>> copyFunctionCache =
            Caffeine.newBuilder().build(CopyableTypes::load);

    private static Optional<Function> load(TypeToken<?> typeToken) {
        Function function = copyFunctions.get(typeToken);
        if (function != null) {
            return Optional.of(function);
        }
        for (TypeToken<?> typeToken1 : typeToken.getTypes()) {
            function = copyFunctions.get(typeToken1);
            if (function != null) {
                return Optional.of(function);
            }
        }
        return Optional.empty();
    }

    static <T> Optional<T> copy(T object) {
        return object == null ? Optional.empty() : object instanceof Copyable ?
                Optional.of(((Copyable<T>) object).copy()) : copy0(object);
    }

    static <T> T copyOrSelf(T object) {
        return object == null ? null : object instanceof Copyable ? ((Copyable<T>) object).copy() : copy0(object).orElse(object);
    }

    private static <T> Optional<T> copy0(T object) {
        final Optional<Function> optFunction = copyFunctionCache.get(TypeToken.of(object.getClass()));
        return (Optional) optFunction.map(f -> f.apply(object));
    }

    static <T> void register(TypeToken<T> type, Function<T, T> copyFunction) {
        checkNotNull(type, "type");
        checkNotNull(copyFunction, "copyFunction");
        copyFunctions.put(type, copyFunction);
    }
}

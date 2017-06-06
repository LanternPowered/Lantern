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

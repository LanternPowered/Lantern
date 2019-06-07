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
package org.lanternpowered.server.data;

import static org.lanternpowered.server.util.ReflectionHelper.createUnsafeInstance;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.Value;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class ImmutableDataCachingUtil {

    private ImmutableDataCachingUtil() {}

    public static final int CACHE_LIMIT_FOR_INDIVIDUAL_TYPE = 100;
    public static final int MANIPULATOR_CACHE_LIMIT = 100000;
    public static final int VALUE_CACHE_LIMIT = 100000;

    private static final Cache<String, ImmutableDataManipulator<?, ?>> manipulatorCache = CacheBuilder.newBuilder()
            .maximumSize(MANIPULATOR_CACHE_LIMIT)
            .concurrencyLevel(4)
            .build();

    private static final Cache<String, Value.Immutable<?>> valueCache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .maximumSize(VALUE_CACHE_LIMIT)
            .build();

    /**
     * Retrieves a basic manipulator from {@link Cache}. If the {@link Cache}
     * does not have the desired {@link ImmutableDataManipulator} with relative
     * values, a new one is created and submitted to the cache for future
     * retrieval.
     *
     * <p>Note that two instances of an {@link ImmutableDataManipulator} may be
     * equal to each other, but they may not be the same instance, this is due
     * to caching and outside instantiation.</p>
     *
     * @param immutableClass The immutable manipulator class to get an instance of
     * @param args The arguments to pass to the constructor
     * @param <T> The type of immutable data manipulator
     * @return The newly created immutable data manipulators
     */
    @SuppressWarnings("unchecked")
    public static <T extends ImmutableDataManipulator<?, ?>> T getManipulator(final Class<T> immutableClass, final Object... args) {
        final String key = getKey(immutableClass, args);
        // We can't really use the generic typing here because it's complicated...
        try {
            // Let's get the key
            return (T) ImmutableDataCachingUtil.manipulatorCache.get(key, () -> {
                try {
                    return createUnsafeInstance(immutableClass, args);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    Lantern.getLogger().error("Could not construct an ImmutableDataManipulator: " + immutableClass.getCanonicalName() +
                            " with the args: " + Arrays.toString(args), e);
                }
                throw new UnsupportedOperationException("Could not construct the ImmutableDataManipulator: " + immutableClass.getName() +
                        " with the args: " + Arrays.toString(args));
            });
        } catch (Throwable e) {
            throw new UnsupportedOperationException("Could not construct the ImmutableDataManipulator: " + immutableClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E, V extends Value.Immutable<?>, T extends Value.Immutable<E>> T getValue(final Class<V> valueClass,
            final Key<? extends Value<E>> usedKey, final E defaultArg, @Nullable final E arg, @Nullable final Object... extraArgs) {
        final String key = getKey(valueClass, usedKey.getQuery().asString('.'), arg == null ? defaultArg.getClass() : arg.getClass(), arg);
        try {
            return (T) ImmutableDataCachingUtil.valueCache.get(key, (Callable<Value.Immutable<?>>) () -> {
                try {
                    if (extraArgs == null || extraArgs.length == 0) {
                        return createUnsafeInstance(valueClass, usedKey, defaultArg, arg);
                    } else {
                        return createUnsafeInstance(valueClass, usedKey, defaultArg, arg, extraArgs);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    Lantern.getLogger().error("Could not construct an ImmutableValue: " + valueClass.getCanonicalName(), e);
                }
                throw new UnsupportedOperationException("Could not construct the ImmutableValue: " + valueClass.getName());
            });
        } catch (ExecutionException e) {
            throw new UnsupportedOperationException("Could not construct the ImmutableValue: " + valueClass.getName(), e);
        }
    }

    private static String getKey(final Class<?> immutableClass, final Object... args) {
        final StringBuilder builder = new StringBuilder(immutableClass.getCanonicalName() + ":");
        for (Object object : args) {
            if (object instanceof CatalogType) {
                builder.append("{").append(((CatalogType) object).getKey()).append("}");
            } else {
                builder.append("{").append(object.toString()).append("}");
            }
        }
        return builder.toString();
    }
}

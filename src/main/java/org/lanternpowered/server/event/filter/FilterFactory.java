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
package org.lanternpowered.server.event.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.lanternpowered.server.util.DefineableClassLoader;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class FilterFactory {

    private final AtomicInteger id = new AtomicInteger();
    private final DefineableClassLoader classLoader;
    private final LoadingCache<Method, Class<? extends EventFilter>> cache =
            Caffeine.newBuilder().weakValues().build((CacheLoader<Method, Class<? extends EventFilter>>) this::createClass);
    private final String targetPackage;

    public FilterFactory(String targetPackage, DefineableClassLoader classLoader) {
        checkNotNull(targetPackage, "targetPackage");
        checkArgument(!targetPackage.isEmpty(), "targetPackage cannot be empty");
        this.targetPackage = targetPackage + '.';
        this.classLoader = checkNotNull(classLoader, "classLoader");
    }

    @Nullable
    public Class<? extends EventFilter> createFilter(Method method) throws Exception {
        return this.cache.get(method);
    }

    private Class<? extends EventFilter> createClass(Method method) {
        final Class<?> handle = method.getDeclaringClass();
        final Class<?> eventClass = method.getParameterTypes()[0];
        final String name = this.targetPackage + eventClass.getSimpleName() + "Filter_" + handle.getSimpleName() + '_'
                + method.getName() + this.id.incrementAndGet();
        final byte[] cls = FilterGenerator.get().generateClass(name, method);
        return this.classLoader.defineClass(name, cls);
    }
}

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

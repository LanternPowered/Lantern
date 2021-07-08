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
package org.lanternpowered.server.util.supplier;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.lanternpowered.server.util.LambdaFactory;

import java.util.function.Supplier;

/**
 * A generator for {@link Supplier} to supply
 * classes with empty constructors.
 */
@SuppressWarnings("unchecked")
public final class ObjectSupplierGenerator {

    /**
     * Attempts to generate a {@link Supplier} for the
     * given object class. The object must have a empty
     * constructor.
     *
     * @param objectType The object class
     * @param <T> The object type
     * @return The supplier
     */
    public static <T> Supplier<T> getSupplier(Class<T> objectType) {
        checkNotNull(objectType, "objectType");
        return (Supplier<T>) supplierCache.get(objectType);
    }

    /**
     * A cache for all the {@link Supplier}s.
     */
    private static final LoadingCache<Class<?>, Supplier<?>> supplierCache =
            Caffeine.newBuilder().weakValues().build(LambdaFactory::createSupplier);
}
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
package org.lanternpowered.server.util;

import io.netty.util.concurrent.FastThreadLocal;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * This is a {@link ThreadLocal} that uses {@link SoftReference}s to store
 * the values to prevent possible memory leaks for some use purposes.
 *
 * @param <T> the type of the value
 */
public class FastSoftThreadLocal<T> {

    private final FastThreadLocal<SoftReference<T>> threadLocal;

    /**
     * Creates a thread local variable. The initial value of the variable is
     * determined by invoking the {@link Supplier#get()} method.
     *
     * @param supplier the supplier to be used to determine the initial value
     * @return a new thread local variable
     */
    public static <S> FastSoftThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedSoftThreadLocal<>(supplier);
    }

    /**
     * Creates a thread local variable.
     *
     * @see #withInitial(Supplier)
     */
    public FastSoftThreadLocal() {
        this(new FastThreadLocal<>());
    }

    private FastSoftThreadLocal(FastThreadLocal<SoftReference<T>> threadLocal) {
        this.threadLocal = threadLocal;
    }

    /**
     * Gets the current thread's value for this thread-local
     * variable.
     *
     * @return the value
     */
    public T get() {
        final SoftReference<T> ref = this.threadLocal.get();
        if (ref != null) {
            T value = ref.get();
            if (value == null) {
                this.set(value = this.initialValue());
            }
            return value;
        }
        return null;
    }

    /**
     * Removes the current thread's value for this thread-local
     * variable.
     */
    public void remove() {
        this.threadLocal.remove();
    }

    /**
     * Sets the current thread's value for this thread-local
     * variable.
     *
     * @param value the value
     */
    public void set(T value) {
        this.threadLocal.set(value == null ? null : new SoftReference<>(value));
    }

    T initialValue() {
        return null;
    }

    private static final class SuppliedSoftThreadLocal<T> extends FastSoftThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        SuppliedSoftThreadLocal(Supplier<? extends T> supplier) {
            super(create(supplier));
            this.supplier = supplier;
        }

        private static <T> FastThreadLocal<SoftReference<T>> create(Supplier<? extends T> supplier) {
            requireNonNull(supplier);
            return FastThreadLocals.withInitial(() -> {
                final T value = supplier.get();
                return value == null ? null : new SoftReference<>(value);
            });
        }

        @Override
        T initialValue() {
            return this.supplier.get();
        }
    }

}

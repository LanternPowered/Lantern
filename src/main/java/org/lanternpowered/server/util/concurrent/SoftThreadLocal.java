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
package org.lanternpowered.server.util.concurrent;

import static java.util.Objects.requireNonNull;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

/**
 * This is a {@link ThreadLocal} that uses {@link SoftReference}s to store
 * the values to prevent possible memory leaks for some use purposes.
 *
 * @param <T> The type of the value
 */
public class SoftThreadLocal<T> {

    private final ThreadLocal<SoftReference<T>> threadLocal;

    /**
     * Creates a thread local variable. The initial value of the variable is
     * determined by invoking the {@link Supplier#get()} method.
     *
     * @param supplier The supplier to be used to determine the initial value
     * @return A new thread local variable
     */
    public static <S> SoftThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedSoftThreadLocal<>(supplier);
    }

    /**
     * Creates a thread local variable.
     *
     * @see #withInitial(java.util.function.Supplier)
     */
    public SoftThreadLocal() {
        this(new ThreadLocal<>());
    }

    private SoftThreadLocal(ThreadLocal<SoftReference<T>> threadLocal) {
        this.threadLocal = threadLocal;
    }

    /**
     * Gets the current thread's value for this thread-local
     * variable.
     *
     * @return The value
     */
    public T get() {
        final SoftReference<T> ref = this.threadLocal.get();
        if (ref != null) {
            T value = ref.get();
            if (value == null) {
                set(value = initialValue());
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
     * @param value The value
     */
    public void set(T value) {
        this.threadLocal.set(value == null ? null : new SoftReference<>(value));
    }

    T initialValue() {
        return null;
    }

    private static final class SuppliedSoftThreadLocal<T> extends SoftThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        SuppliedSoftThreadLocal(Supplier<? extends T> supplier) {
            super(create(supplier));
            this.supplier = supplier;
        }

        private static <T> ThreadLocal<SoftReference<T>> create(Supplier<? extends T> supplier) {
            requireNonNull(supplier);
            return ThreadLocal.withInitial(() -> {
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

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
package org.lanternpowered.server.util;

import static java.util.Objects.requireNonNull;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

/**
 * All {@link Thread}s should be constructed through this helper class
 * to ensure that {@link FastThreadLocalThread}s are used to provide
 * benefits for using {@link FastThreadLocal}s.
 */
public final class ThreadHelper {

    private static final ThreadFactory fastThreadLocalThreadFactory = ThreadHelper::newThread;

    /**
     * Constructs a {@link ThreadFactory} which produces {@link FastThreadLocalThread}s.
     *
     * @return The fast thread factory
     */
    public static ThreadFactory newThreadFactory() {
        return fastThreadLocalThreadFactory;
    }

    /**
     * Constructs a {@link ThreadFactory} which produces {@link FastThreadLocalThread}s
     * which will be named using the name {@link Supplier}.
     *
     * @param nameSupplier The name supplier
     * @return The thread factory
     */
    public static ThreadFactory newThreadFactory(Supplier<String> nameSupplier) {
        requireNonNull(nameSupplier, "nameSupplier");
        return runnable -> newThread(runnable, nameSupplier.get());
    }

    /**
     * Constructs a new {@link FastThreadLocalThread} for the
     * given {@link Runnable}.
     *
     * @param runnable The runnable
     * @return The thread
     */
    public static Thread newThread(Runnable runnable) {
        requireNonNull(runnable, "runnable");
        return new FastThreadLocalThread(runnable);
    }

    /**
     * Constructs a new {@link FastThreadLocalThread} for the
     * given {@link Runnable} and thread name.
     *
     * @param runnable The runnable
     * @param name The thread name
     * @return The thread
     */
    public static Thread newThread(Runnable runnable, String name) {
        requireNonNull(runnable, "runnable");
        requireNonNull(name, "name");
        return new FastThreadLocalThread(runnable, name);
    }

    private ThreadHelper() {
    }
}

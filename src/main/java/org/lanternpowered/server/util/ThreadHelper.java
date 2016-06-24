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
import io.netty.util.concurrent.FastThreadLocalThread;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class ThreadHelper {

    private ThreadHelper() {
    }

    public static ThreadFactory newFastThreadLocalThreadFactory() {
        return newFastThreadLocalThreadFactory0(null, null);
    }

    public static ThreadFactory newFastThreadLocalThreadFactory(Consumer<Thread> consumer) {
        return newFastThreadLocalThreadFactory0(null, requireNonNull(consumer, "consumer"));
    }

    public static ThreadFactory newFastThreadLocalThreadFactory(Supplier<String> nameSupplier) {
        return newFastThreadLocalThreadFactory0(requireNonNull(nameSupplier, "nameSupplier"), null);
    }

    public static ThreadFactory newFastThreadLocalThreadFactory(Supplier<String> nameSupplier, Consumer<Thread> consumer) {
        return newFastThreadLocalThreadFactory0(requireNonNull(nameSupplier, "nameSupplier"), requireNonNull(consumer, "consumer"));
    }

    private static ThreadFactory newFastThreadLocalThreadFactory0(@Nullable Supplier<String> nameSupplier,
            @Nullable Consumer<Thread> consumer) {
        return runnable -> {
            final Thread thread = newFastThreadLocalThread0(thr -> runnable.run(), nameSupplier == null ? null : nameSupplier.get());
            if (consumer != null) {
                consumer.accept(thread);
            }
            return thread;
        };
    }

    public static Thread newFastThreadLocalThread(Consumer<Thread> runnable) {
        return newFastThreadLocalThread0(runnable, null);
    }

    public static Thread newFastThreadLocalThread(Consumer<Thread> runnable, String name) {
        return newFastThreadLocalThread0(runnable, requireNonNull(name, "name"));
    }

    public static Thread newFastThreadLocalThread(Runnable runnable) {
        requireNonNull(runnable, "runnable");
        return newFastThreadLocalThread0(thread -> runnable.run(), null);
    }

    public static Thread newFastThreadLocalThread(Runnable runnable, String name) {
        requireNonNull(runnable, "runnable");
        return newFastThreadLocalThread0(thread -> runnable.run(), requireNonNull(name, "name"));
    }

    private static Thread newFastThreadLocalThread0(Consumer<Thread> runnable, @Nullable String name) {
        requireNonNull(runnable, "runnable");
        final Consumer<Thread> runnable1 = thread0 -> {
            try {
                runnable.accept(thread0);
            } finally {
                FastThreadLocal.removeAll();
            }
        };
        final Thread thread;
        if (name != null) {
            thread = new FastThreadLocalThread(name) {
                @Override
                public void run() {
                    runnable1.accept(this);
                }
            };
        } else {
            thread = new FastThreadLocalThread() {
                @Override
                public void run() {
                    runnable1.accept(this);
                }
            };
        }
        return thread;
    }
}

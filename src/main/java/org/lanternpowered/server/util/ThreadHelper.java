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
package org.lanternpowered.server.util;

import static java.util.Objects.requireNonNull;

import io.netty.util.concurrent.FastThreadLocal;
import org.lanternpowered.api.cause.CauseStack;

import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

/**
 * All {@link Thread}s should be constructed through this helper class
 * to ensure that {@link LanternThread}s are used to provide benefits
 * for using {@link FastThreadLocal}s and fast per thread {@link CauseStack}s.
 */
public final class ThreadHelper {

    private static final ThreadFactory lanternThreadFactory = ThreadHelper::newThread;

    /**
     * Constructs a {@link ThreadFactory} which produces {@link LanternThread}s.
     *
     * @return The fast thread factory
     */
    public static ThreadFactory newThreadFactory() {
        return lanternThreadFactory;
    }

    /**
     * Constructs a {@link ThreadFactory} which produces {@link LanternThread}s
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
     * Constructs a new {@link LanternThread} for the
     * given {@link Runnable}.
     *
     * @param runnable The runnable
     * @return The thread
     */
    public static Thread newThread(Runnable runnable) {
        requireNonNull(runnable, "runnable");
        return new LanternThread(runnable);
    }

    /**
     * Constructs a new {@link LanternThread} for the
     * given {@link Runnable} and thread name.
     *
     * @param runnable The runnable
     * @param name The thread name
     * @return The thread
     */
    public static Thread newThread(Runnable runnable, String name) {
        requireNonNull(runnable, "runnable");
        requireNonNull(name, "name");
        return new LanternThread(runnable, name);
    }

    private ThreadHelper() {
    }
}

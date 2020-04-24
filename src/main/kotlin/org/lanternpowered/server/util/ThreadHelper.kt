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
package org.lanternpowered.server.util

import org.lanternpowered.api.cause.CauseStack
import io.netty.util.concurrent.FastThreadLocal
import java.util.concurrent.ThreadFactory
import java.util.function.Supplier

/**
 * All [Thread]s should be constructed through this helper class
 * to ensure that [LanternThread]s are used to provide benefits
 * for using [FastThreadLocal]s and fast per thread [CauseStack]s.
 */
object ThreadHelper {

    private val lanternThreadFactory = ThreadFactory { runnable -> newThread(runnable) }

    /**
     * Constructs a [ThreadFactory] which produces [LanternThread]s.
     *
     * @return The fast thread factory
     */
    @JvmStatic
    fun newThreadFactory(): ThreadFactory = this.lanternThreadFactory

    /**
     * Constructs a [ThreadFactory] which produces [LanternThread]s
     * which will be named using the name [Supplier].
     *
     * @param nameSupplier The name supplier
     * @return The thread factory
     */
    @JvmStatic
    fun newThreadFactory(nameSupplier: () -> String): ThreadFactory =
            ThreadFactory { runnable -> newThread(runnable, nameSupplier()) }

    /**
     * Constructs a new [LanternThread] for the
     * given [Runnable].
     *
     * @param runnable The runnable
     * @return The thread
     */
    @JvmStatic
    fun newThread(runnable: Runnable): Thread = LanternThread(runnable)

    /**
     * Constructs a new [LanternThread] for the
     * given [Runnable] and thread name.
     *
     * @param runnable The runnable
     * @param name The thread name
     * @return The thread
     */
    @JvmStatic
    fun newThread(runnable: Runnable, name: String): Thread = LanternThread(runnable, name)

    /**
     * Constructs a new [LanternThread] for the
     * given [Runnable] and thread name.
     *
     * @param runnable The runnable
     * @param name The thread name
     * @return The thread
     */
    @JvmStatic
    fun newThread(runnable: () -> Unit, name: String): Thread = newThread(Runnable(runnable), name)
}

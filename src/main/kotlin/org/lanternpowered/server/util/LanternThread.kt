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

import io.netty.util.concurrent.FastThreadLocalThread
import org.lanternpowered.api.cause.CauseStack

/**
 * A [FastThreadLocalThread] which directly stores some objects
 * that will be accessed a lot, like the [CauseStack].
 */
open class LanternThread : FastThreadLocalThread {

    /**
     * The [CauseStack] assigned to this thread, if any.
     */
    var causeStack: CauseStack? = null

    /**
     * Gets the [Throwable] which points to where
     * this [Thread] is constructed.
     */
    /**
     * A [Throwable] which points to where
     * this [Thread] is constructed.
     */
    val constructionSite: Throwable

    /**
     * Constructs a new [LanternThread] with
     * the given [Runnable] task and name.
     *
     * @param target The task
     * @param name The name
     */
    constructor(target: Runnable, name: String) : super(target, name) {
        this.constructionSite = Exception()
    }

    /**
     * Constructs a new [LanternThread] with
     * the given [Runnable] task.
     *
     * @param target The task
     */
    constructor(target: Runnable) : super(target) {
        this.constructionSite = Exception()
    }
}

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
package org.lanternpowered.server.cause

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseContext
import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.cause.CauseStackManagerFrame
import org.lanternpowered.api.util.concurrent.ThreadLocal
import org.lanternpowered.api.x.cause.XCauseStackManager
import org.lanternpowered.server.util.LanternThread

import java.util.Optional

/**
 * A [CauseStackManager] that manages the [LanternCauseStack]s for all
 * the supported [Thread]s. (main, world threads, etc.)
 */
object LanternCauseStackManager : XCauseStackManager {

    /**
     * A [ThreadLocal] to fall back to if a thread doesn't extend [LanternThread].
     */
    private val fallbackCauseStacks = ThreadLocal<LanternCauseStack>()

    /**
     * Sets the [LanternCauseStack] for the current [Thread].
     */
    fun setCurrentCauseStack(causeStack: LanternCauseStack) {
        val thread = Thread.currentThread()
        if (thread is LanternThread) {
            thread.causeStack = causeStack
        } else {
            this.fallbackCauseStacks.set(causeStack)
        }
    }

    override fun currentStackOrNull() = getCauseStackOrNull(Thread.currentThread())

    fun getCauseStackOrNull(thread: Thread)
            = if (thread is LanternThread) thread.causeStack else this.fallbackCauseStacks.get()

    fun getCauseStackOrEmpty(thread: Thread) = getCauseStackOrNull(thread) ?: EmptyCauseStack

    override fun currentStackOrEmpty(): CauseStack = currentStackOrNull() ?: EmptyCauseStack
    override fun currentStack() = currentStackOrNull() ?: throw IllegalStateException("The current thread doesn't support a cause stack.")

    override fun getCurrentCause(): Cause = currentStack().currentCause
    override fun getCurrentContext(): CauseContext = currentStack().currentContext
    override fun pushCause(obj: Any): CauseStackManager = currentStack().pushCause(obj)
    override fun popCause(): Any = currentStack().popCause()
    override fun popCauses(n: Int) = currentStack().popCauses(n)
    override fun peekCause(): Any = currentStack().peekCause()
    override fun pushCauseFrame(): CauseStackManagerFrame = currentStack().pushCauseFrame()
    override fun popCauseFrame(handle: CauseStackManagerFrame) = currentStack().popCauseFrame(handle)
    override fun <T> addContext(key: CauseContextKey<T>, value: T): CauseStackManager = currentStack().addContext(key, value)
    override fun <T> getContext(key: CauseContextKey<T>): Optional<T> = currentStack().getContext(key)
    override fun <T> removeContext(key: CauseContextKey<T>): Optional<T> = currentStack().removeContext(key)
}

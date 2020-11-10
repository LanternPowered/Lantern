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
import org.lanternpowered.api.util.concurrent.ThreadLocal
import org.lanternpowered.server.util.LanternThread

import java.util.Optional
import kotlin.reflect.KClass

/**
 * A [CauseStackManager] that manages the [LanternCauseStack]s for all
 * the supported [Thread]s. (main, world threads, etc.)
 */
object LanternCauseStackManager : CauseStackManager {

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

    override fun currentStackOrNull() = this.getCauseStackOrNull(Thread.currentThread())

    private fun getCauseStackOrNull(thread: Thread)
            = if (thread is LanternThread) thread.causeStack else this.fallbackCauseStacks.get()

    fun getCauseStackOrEmpty(thread: Thread) = this.getCauseStackOrNull(thread) ?: EmptyCauseStack

    override fun currentStackOrEmpty(): CauseStack = this.currentStackOrNull() ?: EmptyCauseStack
    override fun currentStack() = this.currentStackOrNull() ?: error("The current thread doesn't support a cause stack.")

    override fun getCurrentCause(): Cause = this.currentStack().currentCause
    override fun getCurrentContext(): CauseContext = this.currentStack().currentContext
    override fun pushCause(obj: Any): CauseStackManager = apply { this.currentStack().pushCause(obj) }
    override fun popCause(): Any = this.currentStack().popCause()
    override fun popCauses(n: Int) = this.currentStack().popCauses(n)
    override fun peekCause(): Any = this.currentStack().peekCause()
    override fun pushCauseFrame(): CauseStack.Frame = this.currentStack().pushCauseFrame()
    override fun popCauseFrame(handle: org.spongepowered.api.event.CauseStackManager.StackFrame) = this.currentStack().popCauseFrame(handle)
    override fun <T : Any> addContext(key: CauseContextKey<T>, value: T): CauseStackManager = apply { this.currentStack().addContext(key, value) }
    override fun <T : Any> getContext(key: CauseContextKey<T>): Optional<T> = this.currentStack().getContext(key)
    override fun <T : Any> removeContext(key: CauseContextKey<T>): Optional<T> = this.currentStack().removeContext(key)
    override fun <T : Any> get(key: CauseContextKey<T>): T?= this.currentStack()[key]
    override fun <T : Any> set(key: CauseContextKey<T>, value: T) = this.currentStack().set(key, value)
    override fun <T : Any> first(target: KClass<T>): T?  = this.currentStack().first(target)
    override fun <T : Any> first(target: Class<T>): Optional<T> = this.currentStack().first(target)
    override fun <T : Any> last(target: KClass<T>): T? = this.currentStack().last(target)
    override fun <T : Any> last(target: Class<T>): Optional<T> = this.currentStack().last(target)
    override fun containsType(target: KClass<*>): Boolean = this.currentStack().containsType(target)
    override fun containsType(target: Class<*>): Boolean = this.currentStack().containsType(target)
    override fun contains(any: Any): Boolean = this.currentStack().contains(any)
}

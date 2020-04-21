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

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseContext
import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.cause.emptyCauseContext
import org.lanternpowered.api.util.optional.emptyOptional
import java.util.Optional
import kotlin.reflect.KClass

internal object EmptyCauseStack : CauseStack {

    private val obj = Any()
    private val cause by lazy { causeOf(Lantern.game) }
    private val frame = object : CauseStack.Frame {

        override fun close() {}
        override fun getCurrentCause(): Cause = cause
        override fun getCurrentContext(): CauseContext = emptyCauseContext()
        override fun pushCause(obj: Any): CauseStack.Frame = this
        override fun popCause(): Any = obj

        override fun <T : Any> addContext(key: CauseContextKey<T>, value: T) = this
        override fun <T : Any> removeContext(key: CauseContextKey<T>): Optional<T> = emptyOptional()
    }

    override fun getCurrentCause(): Cause = cause
    override fun getCurrentContext(): CauseContext = emptyCauseContext()

    override fun pushCause(obj: Any): CauseStack = this
    override fun popCause(): Any = obj
    override fun popCauses(n: Int) {}
    override fun peekCause(): Any = obj
    override fun pushCauseFrame() = frame
    override fun popCauseFrame(handle: org.spongepowered.api.event.CauseStackManager.StackFrame) {}
    override fun <T : Any> first(target: Class<T>): Optional<T> = emptyOptional()
    override fun <T : Any> first(target: KClass<T>): T? = null
    override fun <T : Any> last(target: Class<T>): Optional<T> = emptyOptional()
    override fun <T : Any> last(target: KClass<T>): T? = null

    override fun containsType(target: Class<*>): Boolean = false
    override fun containsType(target: KClass<*>): Boolean = false
    override fun contains(any: Any): Boolean = false

    override fun <T : Any> addContext(key: CauseContextKey<T>, value: T): EmptyCauseStack = this
    override fun <T : Any> getContext(key: CauseContextKey<T>): Optional<T> = emptyOptional()
    override fun <T : Any> removeContext(key: CauseContextKey<T>): Optional<T> = emptyOptional()
    override fun <T : Any> get(key: CauseContextKey<T>): T? = null
    override fun <T : Any> set(key: CauseContextKey<T>, value: T) {}
}

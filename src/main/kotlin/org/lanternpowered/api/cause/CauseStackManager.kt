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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.cause

import org.lanternpowered.api.Lantern
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Executes the [block] with the given cause applied to this [CauseStack].
 */
inline fun CauseStackManager.withCause(cause: Any, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.currentStack().withCause(cause) {
        block()
    }
}

/**
 * Executes the [block] with the given causes applied to the current stack.
 */
inline fun CauseStackManager.withCauses(iterable: Iterable<Any>, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.currentStack().withCauses(iterable) {
        block()
    }
}

/**
 * Executes the [block] with the given causes applied to the current stack.
 */
inline fun CauseStackManager.withCauses(first: Any, second: Any, vararg more: Any, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.currentStack().withCauses(first, second, *more) {
        block()
    }
}

/**
 * The cause stack manager.
 */
interface CauseStackManager : CauseStack {

    /**
     * Get the current active cause stack, or null if none.
     */
    fun currentStackOrNull(): CauseStack?

    /**
     * Get the current active cause stack, or empty if none.
     */
    fun currentStackOrEmpty(): CauseStack

    /**
     * Gets the current active cause stack, throws an
     * [IllegalStateException] if no stack was found.
     */
    fun currentStack(): CauseStack

    override fun pushCause(obj: Any): CauseStackManager
    override fun <T : Any> addContext(key: CauseContextKey<T>, value: T): CauseStackManager

    /**
     * The singleton instance of the cause stack manager.
     */
    companion object : CauseStackManager by Lantern.causeStackManager
}

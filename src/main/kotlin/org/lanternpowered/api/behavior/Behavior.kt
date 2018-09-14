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
@file:Suppress("FunctionName")

package org.lanternpowered.api.behavior

/**
 * A convenient constructor to allow to construct the [Behavior]
 * through a SAM like conversion.
 */
inline fun Behavior(crossinline fn: (BehaviorType, BehaviorContext) -> Boolean) = object : Behavior {
    override fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean = fn(type, ctx)
}

@FunctionalInterface
interface Behavior {

    /**
     * Applies this behavior to the given [BehaviorContext]. Returns
     * whether the behavior was applied successfully.
     *
     * @param type The behavior type which is currently being executed
     * @param ctx The behavior context
     * @return Whether the behavior was applied successfully
     */
    fun apply(type: BehaviorType, ctx: BehaviorContext): Boolean

    /**
     * Tries to apply this behavior to the given [BehaviorContext]. Returns
     * whether the behavior was applied successfully. When this behavior fails,
     * the context will be reset to the state before it was executed.
     *
     * @param type The behavior type which is currently being executed
     * @param ctx The behavior context
     * @return Whether the behavior was applied successfully
     */
    @JvmDefault
    fun tryApply(type: BehaviorType, ctx: BehaviorContext): Boolean {
        val snapshot = ctx.createSnapshot()
        if (apply(type, ctx)) {
            return true
        }
        ctx.restoreSnapshot(snapshot)
        return false
    }

    /**
     * Returns a new [Behavior] that will be
     * executed when this behavior is executed.
     *
     * The result of the combined behavior will
     * be true if BOTH of the behaviors were successful.
     *
     * @param behavior The other behavior
     * @return The combined behavior
     */
    @JvmDefault
    fun andThen(behavior: Behavior) = Behavior { type, ctx ->
        tryApply(type, ctx)
        behavior.tryApply(type, ctx)
    }

    /**
     * Returns a new [Behavior] that will be executed
     * if the current behavior was executed successfully.
     *
     * The result of the combined behavior will
     * be the result of the other behavior.
     *
     * @param behavior The other behavior
     * @return The combined behavior
     */
    @JvmDefault
    fun andThenIfSuccessful(behavior: Behavior) = Behavior { type, ctx -> tryApply(type, ctx) && behavior.tryApply(type, ctx) }

    /**
     * Returns a new [Behavior] that will be executed
     * if the current behavior was executed successfully.
     *
     * The result of the combined behavior will
     * be the result of the other behavior.
     *
     * @param behavior The other behavior
     * @return The combined behavior
     */
    @JvmDefault
    fun andThenIfFailure(behavior: Behavior) = Behavior { type, ctx -> !tryApply(type, ctx) && behavior.tryApply(type, ctx) }
}

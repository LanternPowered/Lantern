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
package org.lanternpowered.api.cause

import org.lanternpowered.api.Lantern
import java.util.Optional

/**
 * A [CauseStack] for a specific [Thread].
 */
interface CauseStack : CauseStackManager {

    /**
     * Gets the first [T] object of this [Cause], if available.
     *
     * @param target The class of the target type
     * @param T The type of object being queried for
     * @return The first element of the type, if available
     */
    fun <T> first(target: Class<T>): Optional<T>

    /**
     * Gets the last object instance of the [Class] of type [T].
     *
     * @param target The class of the target type
     * @param T The type of object being queried for
     * @return The last element of the type, if available
     */
    fun <T> last(target: Class<T>): Optional<T>

    /**
     * Returns whether the target class matches any object of this [Cause].
     *
     * @param target The class of the target type
     * @return True if found, false otherwise
     */
    fun containsType(target: Class<*>): Boolean

    /**
     * Checks if this cause contains of any of the provided [Object]. This
     * is the equivalent to checking based on [.equals] for each
     * object in this cause.
     *
     * @param any The object to check if it is contained
     * @return True if the object is contained within this cause
     */
    operator fun contains(any: Any): Boolean

    override fun pushCauseFrame(): Frame
    override fun pushCause(obj: Any): CauseStack
    override fun <T> addContext(key: CauseContextKey<T>, value: T): CauseStack

    interface Frame : CauseStackManagerFrame {

        override fun pushCause(obj: Any): Frame
        override fun <T> addContext(key: CauseContextKey<T>, value: T): Frame
    }

    companion object {

        /**
         * Gets the [CauseStack] for the current [Thread].
         * `null` will be returned if the thread isn't supported.
         *
         * @return The cause stack
         */
        @JvmStatic
        fun currentOrNull() = Lantern.causeStackManager.currentStackOrNull()

        /**
         * Gets the [CauseStack] for the current [Thread]. A
         * empty [CauseStack] will be returned if the thread
         * isn't supported.
         *
         * @return The cause stack
         */
        @JvmStatic
        fun currentOrEmpty() = Lantern.causeStackManager.currentStackOrEmpty()

        /**
         * Gets the [CauseStack] for the current [Thread]. A
         * [IllegalStateException] will be thrown if the current thread
         * isn't supported.
         *
         * @return The cause stack
         */
        @JvmStatic
        fun current() = Lantern.causeStackManager.currentStack()
    }
}

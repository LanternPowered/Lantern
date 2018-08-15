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
package org.lanternpowered.api.inject.lazy

import com.google.inject.ProvidedBy
import org.lanternpowered.api.ext.*

/**
 * Represents a value that will be instantiated lazily.
 */
@FunctionalInterface
@ProvidedBy(LazyProvider::class)
interface Lazy<T> : kotlin.Lazy<T> {

    // Some convenient methods to access the value
    operator fun invoke(): T = this.value
    fun get() = this.value

    companion object {

        /**
         * Constructs a new [Lazy] with the given initializer.
         */
        fun <T> of(initializer: () -> T): Lazy<T> = LazyImpl(initializer)
    }
}

internal class LazyImpl<T>(initializer: () -> T) : Lazy<T> {

    private var backing: Any? = uninitializedValue
    private var initializer: (() -> T)? = initializer

    override val value: T
        get() {
            if (this.backing === uninitializedValue) {
                this.backing = this.initializer!!()
                this.initializer = null
            }
            return this.backing.uncheckedCast()
        }

    override fun isInitialized() = this.backing !== uninitializedValue
    override fun toString() = if (isInitialized()) this.backing.toString() else "Lazy value not initialized yet."

    companion object {
        private val uninitializedValue = Object()
    }
}

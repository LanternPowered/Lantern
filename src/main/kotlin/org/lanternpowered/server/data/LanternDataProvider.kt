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
package org.lanternpowered.server.data

import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

internal open class LanternDataProvider<V : Value<E>, E : Any>(
        private val key: Key<V>,
        private val allowAsyncAccess: DataHolder.() -> Boolean,
        private val supportedByTester: DataHolder.() -> Boolean,
        private val removeHandler: DataHolder.Mutable.() -> DataTransactionResult,
        private val removeFastHandler: DataHolder.Mutable.() -> Boolean,
        private val offerValueHandler: DataHolder.Mutable.(value: V) -> DataTransactionResult,
        private val offerValueFastHandler: DataHolder.Mutable.(value: V) -> Boolean,
        private val offerHandler: DataHolder.Mutable.(element: E) -> DataTransactionResult,
        private val offerFastHandler: DataHolder.Mutable.(element: E) -> Boolean,
        private val getHandler: DataHolder.() -> E?,
        private val getValueHandler: DataHolder.() -> V?,
        private val withHandler: DataHolder.Immutable<*>.(element: E) -> DataHolder.Immutable<*>?,
        private val withValueHandler: DataHolder.Immutable<*>.(element: V) -> DataHolder.Immutable<*>?,
        private val withoutHandler: DataHolder.Immutable<*>.() -> DataHolder.Immutable<*>?
) : IDataProvider<V, E> {

    override fun getKey() = this.key

    override fun offer(container: DataHolder.Mutable, element: E) =
            this.offerHandler(container, element)

    override fun offerFast(container: DataHolder.Mutable, element: E) =
            this.offerFastHandler(container, element)

    override fun offerValue(container: DataHolder.Mutable, value: V) =
            this.offerValueHandler(container, value)

    override fun offerValueFast(container: DataHolder.Mutable, value: V) =
            this.offerValueFastHandler(container, value)

    override fun allowsAsynchronousAccess(container: DataHolder) =
            this.allowAsyncAccess(container)

    override fun get(container: DataHolder) =
            this.getHandler(container).optional()

    override fun getValue(container: DataHolder) =
            this.getValueHandler(container).optional()

    override fun isSupported(container: DataHolder) =
            this.supportedByTester(container)

    override fun remove(container: DataHolder.Mutable) =
            this.removeHandler(container)

    override fun removeFast(container: DataHolder.Mutable) =
            this.removeFastHandler(container)

    override fun <I : DataHolder.Immutable<I>> with(immutable: I, element: E) =
            this.withHandler(immutable, element).uncheckedCast<I>().optional()

    override fun <I : DataHolder.Immutable<I>> withValue(immutable: I, value: V) =
            this.withValueHandler(immutable, value).uncheckedCast<I>().optional()

    override fun <I : DataHolder.Immutable<I>> without(immutable: I) =
            this.withoutHandler(immutable).uncheckedCast<I>().optional()
}

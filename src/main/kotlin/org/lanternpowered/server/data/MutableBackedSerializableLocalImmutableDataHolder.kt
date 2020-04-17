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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data

import org.lanternpowered.api.ext.optional
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import java.util.Objects
import java.util.Optional

abstract class MutableBackedSerializableLocalImmutableDataHolder<I : DataHolder.Immutable<I>, M : SerializableLocalMutableDataHolder>(
        protected val backingDataHolder: M
) : LocalImmutableDataHolder<I> {

    override val keyRegistry: LocalKeyRegistry<out LocalImmutableDataHolder<I>> get() = this.backingDataHolder.keyRegistry.forHolderUnchecked()

    override fun with(value: Value<*>): Optional<I> {
        val copy = this.backingDataHolder.copy() as M
        return if (copy.offerFast(value)) {
            withBacking(copy).optional()
        } else super.with(value)
    }

    override fun <E : Any> with(key: Key<out Value<E>>, value: E): Optional<I> {
        val copy = this.backingDataHolder.copy() as M
        return if (copy.offerFast(key, value)) {
            withBacking(copy).optional()
        } else super.with(key, value)
    }

    override fun without(key: Key<*>): Optional<I> {
        val copy = this.backingDataHolder.copy() as M
        return if (copy.removeFast(key)) {
            withBacking(copy).optional()
        } else super.without(key)
    }

    override fun merge(that: I, function: MergeFunction): I {
        val copy = this.backingDataHolder.copy() as M
        copy.copyFromNoEvents((that as MutableBackedSerializableLocalImmutableDataHolder<*,*>).backingDataHolder, function)
        return withBacking(copy)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other.javaClass != this.javaClass) {
            return false
        }
        other as MutableBackedSerializableLocalImmutableDataHolder<*,*>
        return this.backingDataHolder == other.backingDataHolder
    }

    override fun hashCode(): Int = Objects.hash(this.javaClass, this.backingDataHolder)

    /**
     * Constructs a new immutable data holder with the backing mutable data holder.
     *
     * @param backingDataHolder The backing data holder
     * @return The immutable holder
     */
    protected abstract fun withBacking(backingDataHolder: M): I
}

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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data

import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.data.CopyableDataHolder
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import java.util.Objects
import java.util.Optional

abstract class MutableBackedCopyableLocalImmutableDataHolder<I, M : CopyableLocalMutableDataHolder>(
        protected val backingDataHolder: M
) : CopyableLocalImmutableDataHolder<I> where I : DataHolder.Immutable<I>, I : CopyableDataHolder {

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

    override fun mergeWith(that: I, function: MergeFunction): I {
        val copy = this.backingDataHolder.copy() as M
        copy.copyFromNoEvents((that as MutableBackedCopyableLocalImmutableDataHolder<*,*>).backingDataHolder, function)
        return withBacking(copy)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other.javaClass != this.javaClass) {
            return false
        }
        other as MutableBackedCopyableLocalImmutableDataHolder<*,*>
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

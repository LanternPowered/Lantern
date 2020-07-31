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
package org.lanternpowered.server.data

import org.lanternpowered.api.util.optional.asOptional
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
            this.getHandler(container).asOptional()

    override fun getValue(container: DataHolder) =
            this.getValueHandler(container).asOptional()

    override fun isSupported(container: DataHolder) =
            this.supportedByTester(container)

    override fun remove(container: DataHolder.Mutable) =
            this.removeHandler(container)

    override fun removeFast(container: DataHolder.Mutable) =
            this.removeFastHandler(container)

    override fun <I : DataHolder.Immutable<I>> with(immutable: I, element: E) =
            this.withHandler(immutable, element).uncheckedCast<I>().asOptional()

    override fun <I : DataHolder.Immutable<I>> withValue(immutable: I, value: V) =
            this.withValueHandler(immutable, value).uncheckedCast<I>().asOptional()

    override fun <I : DataHolder.Immutable<I>> without(immutable: I) =
            this.withoutHandler(immutable).uncheckedCast<I>().asOptional()
}

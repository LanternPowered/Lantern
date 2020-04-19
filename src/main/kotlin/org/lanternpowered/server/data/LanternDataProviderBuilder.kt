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

import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

internal class LanternDataProviderBuilder<V : Value<E>, E : Any>(key: Key<V>) :
        LanternDataProviderBuilderBase<V, E>(key), DataProviderBuilder<V, E> {

    override fun allowAsyncAccess(tester: DataHolder.() -> Boolean) = apply {
        this.allowAsyncAccess = tester
    }

    override fun allowAsyncAccess() = allowAsyncAccess(alwaysAsyncAccess)

    override fun supportedBy(tester: DataHolder.() -> Boolean) = apply { this.supportedByHandler = tester }

    override fun remove(handler: DataHolder.Mutable.() -> DataTransactionResult) = apply {
        this.removeHandler = handler
    }

    override fun removeFast(handler: DataHolder.Mutable.() -> Boolean) = apply {
        this.removeFastHandler = handler
    }

    override fun offer(handler: DataHolder.Mutable.(element: E) -> DataTransactionResult) = apply {
        this.offerHandler = handler
    }

    override fun offerFast(handler: DataHolder.Mutable.(element: E) -> Boolean) = apply {
        this.offerFastHandler = handler
    }

    override fun offerValue(handler: DataHolder.Mutable.(value: V) -> DataTransactionResult) = apply {
        this.offerValueHandler = handler
    }

    override fun offerValueFast(handler: DataHolder.Mutable.(value: V) -> Boolean) = apply {
        this.offerValueFastHandler = handler
    }

    override fun <I : DataHolder.Immutable<I>> with(handler: I.(element: E) -> I?) = apply {
        this.withHandler = handler.uncheckedCast()
    }

    override fun <I : DataHolder.Immutable<I>> withValue(handler: I.(element: V) -> I?) = apply {
        this.withValueHandler = handler.uncheckedCast()
    }

    override fun <I : DataHolder.Immutable<I>> without(handler: I.() -> I?) = apply {
        this.withoutHandler = handler.uncheckedCast()
    }

    override fun get(handler: DataHolder.() -> E?) = apply { this.getHandler = handler }

    override fun getValue(handler: DataHolder.() -> V?) = apply { this.getValueHandler = handler }
}

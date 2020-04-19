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
import org.spongepowered.api.data.DirectionRelativeDataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction

internal class LanternLocalDataProviderBuilder<V : Value<E>, E : Any, H : DataHolder>(key: Key<V>) :
        LanternDataProviderBuilderBase<V, E>(key), LocalDataProviderBuilder<V, E, H>, LocalJDataProviderBuilder<V, E, H> {

    override fun allowAsyncAccess() = apply { this.allowAsyncAccess = alwaysAsyncAccess }

    override fun supportedBy(tester: H.() -> Boolean) = apply {
        this.supportedByHandler = tester.uncheckedCast()
    }

    // Unchecked casts here, is needed to prevent weird kotlin issue

    override fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.remove(
            handler: H.() -> DataTransactionResult) = apply {
        this@LanternLocalDataProviderBuilder.removeHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.removeFast(
            handler: H.() -> Boolean) = apply {
        this@LanternLocalDataProviderBuilder.removeFastHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.offer(
            handler: H.(element: E) -> DataTransactionResult) = apply {
        this@LanternLocalDataProviderBuilder.offerHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.offerFast(
            handler: H.(element: E) -> Boolean) = apply {
        this@LanternLocalDataProviderBuilder.offerFastHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.offerValue(
            handler: H.(value: V) -> DataTransactionResult) = apply {
        this@LanternLocalDataProviderBuilder.offerValueHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Mutable> LocalDataProviderBuilder<V, E, H>.offerValueFast(
            handler: H.(value: V) -> Boolean) = apply {
        this@LanternLocalDataProviderBuilder.offerValueFastHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Immutable<H>> LocalDataProviderBuilder<V, E, H>.with(
            handler: H.(element: E) -> H?) = apply {
        this@LanternLocalDataProviderBuilder.withHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Immutable<H>> LocalDataProviderBuilder<V, E, H>.withValue(
            handler: H.(element: V) -> H?) = apply {
        this@LanternLocalDataProviderBuilder.withValueHandler = handler.uncheckedCast()
    }

    override fun <H : DataHolder.Immutable<H>> LocalDataProviderBuilder<V, E, H>.without(
            handler: H.() -> H?) = apply {
        this@LanternLocalDataProviderBuilder.withoutHandler = handler.uncheckedCast()
    }

    override fun <H : DirectionRelativeDataHolder> LocalDataProviderBuilder<V, E, H>.getDirectional(
            handler: H.(direction: Direction) -> E?) = apply {
        this@LanternLocalDataProviderBuilder.getDirectionalHandler = handler.uncheckedCast()
    }

    override fun <H : DirectionRelativeDataHolder> LocalDataProviderBuilder<V, E, H>.getValueDirectional(
            handler: H.(direction: Direction) -> V?) = apply {
        this@LanternLocalDataProviderBuilder.getValueDirectionalHandler = handler.uncheckedCast()
    }

    override fun removeFast(handler: H.() -> Boolean) = apply {
        this.removeFastHandler = handler.uncheckedCast()
    }

    override fun remove(handler: H.() -> DataTransactionResult) = apply {
        this.removeHandler = handler.uncheckedCast()
    }

    override fun offerFast(handler: H.(element: E) -> Boolean) = apply {
        this.offerFastHandler = handler.uncheckedCast()
    }

    override fun offerValue(handler: H.(value: V) -> DataTransactionResult) = apply {
        this.offerValueHandler = handler.uncheckedCast()
    }

    override fun offerValueFast(handler: H.(value: V) -> Boolean) = apply {
        this.offerValueFastHandler = handler.uncheckedCast()
    }

    override fun offer(handler: H.(element: E) -> DataTransactionResult) = apply {
        this.offerHandler = handler.uncheckedCast()
    }

    override fun get(handler: H.() -> E?) = apply {
        this.getHandler = handler.uncheckedCast()
    }

    override fun getValue(handler: H.() -> V?) = apply {
        this.getValueHandler = handler.uncheckedCast()
    }

    override fun getDirectional(handler: H.(direction: Direction) -> E?) = apply {
        this.getDirectionalHandler = handler.uncheckedCast()
    }

    override fun getValueDirectional(handler: H.(direction: Direction) -> V?) = apply {
        this.getValueDirectionalHandler = handler.uncheckedCast()
    }
}

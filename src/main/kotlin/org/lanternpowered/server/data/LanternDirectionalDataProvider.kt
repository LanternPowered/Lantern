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

import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction
import java.util.Optional

internal class LanternDirectionalDataProvider<V : Value<E>, E : Any>(
        key: Key<V>,
        allowAsyncAccess: DataHolder.() -> Boolean,
        val supportedByTester: DataHolder.() -> Boolean,
        removeHandler: DataHolder.Mutable.() -> DataTransactionResult,
        removeFastHandler: DataHolder.Mutable.() -> Boolean,
        offerValueHandler: DataHolder.Mutable.(value: V) -> DataTransactionResult,
        offerValueFastHandler: DataHolder.Mutable.(value: V) -> Boolean,
        offerHandler: DataHolder.Mutable.(element: E) -> DataTransactionResult,
        offerFastHandler: DataHolder.Mutable.(element: E) -> Boolean,
        val getHandler: DataHolder.() -> E?,
        val getValueHandler: DataHolder.() -> V?,
        val getDirectionalHandler: DataHolder.(direction: Direction) -> E?,
        val getValueDirectionalHandler: DataHolder.(direction: Direction) -> V?,
        withHandler: DataHolder.Immutable<*>.(element: E) -> DataHolder.Immutable<*>?,
        withValueHandler: DataHolder.Immutable<*>.(element: V) -> DataHolder.Immutable<*>?,
        withoutHandler: DataHolder.Immutable<*>.() -> DataHolder.Immutable<*>?
) : LanternDataProvider<V, E>(key, allowAsyncAccess, supportedByTester, removeHandler,
        removeFastHandler, offerValueHandler, offerValueFastHandler, offerHandler,
        offerFastHandler, getHandler, getValueHandler, withHandler, withValueHandler, withoutHandler
), IDirectionalDataProvider<V, E> {

    override fun get(container: DataHolder): Optional<E> =
            this.getHandler(container).optional()

    override fun getValue(container: DataHolder): Optional<V> =
            this.getValueHandler(container).optional()

    override fun get(dataHolder: DataHolder, direction: Direction): Optional<E> =
            this.getDirectionalHandler(dataHolder, direction).optional()

    override fun getValue(dataHolder: DataHolder, direction: Direction): Optional<V> =
            this.getValueDirectionalHandler(dataHolder, direction).optional()

    override fun isSupported(container: DataHolder): Boolean =
            this.supportedByTester(container)

    override fun isSupported(dataHolder: DataHolder, direction: Direction) = isSupported(dataHolder)
}

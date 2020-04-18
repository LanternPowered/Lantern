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

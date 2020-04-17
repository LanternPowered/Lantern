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

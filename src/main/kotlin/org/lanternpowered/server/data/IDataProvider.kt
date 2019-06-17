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

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ext.*
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

interface IDataProvider<V : Value<E>, E : Any> : DataProvider<V, E> {

    override fun getKey(): Key<V>

    override fun isSupported(container: DataHolder): Boolean

    @JvmDefault
    fun allowsAsynchronousAccess(container: DataHolder): Boolean {
        return allowsAsynchronousAccess(container.javaClass.typeToken)
    }

    override fun allowsAsynchronousAccess(token: TypeToken<out DataHolder>): Boolean

    @JvmDefault
    override fun getValue(container: DataHolder): Optional<V> = super.getValue(container)

    override fun get(container: DataHolder): Optional<E>

    override fun offer(container: DataHolder.Mutable, element: E): DataTransactionResult

    @JvmDefault
    fun offerFast(container: DataHolder.Mutable, element: E) = offer(container, element).isSuccessful

    @JvmDefault
    override fun offerValue(container: DataHolder.Mutable, value: V): DataTransactionResult = super.offerValue(container, value)

    @JvmDefault
    fun offerValueFast(container: DataHolder.Mutable, value: V) = offerValue(container, value).isSuccessful

    override fun remove(container: DataHolder.Mutable): DataTransactionResult

    @JvmDefault
    fun removeFast(container: DataHolder.Mutable) = remove(container).isSuccessful

    override fun <I : DataHolder.Immutable<I>> with(immutable: I, element: E): Optional<I>

    @JvmDefault
    override fun <I : DataHolder.Immutable<I>> withValue(immutable: I, value: V): Optional<I> = super.withValue(immutable, value)

    override fun <I : DataHolder.Immutable<I>> without(immutable: I): Optional<I>
}

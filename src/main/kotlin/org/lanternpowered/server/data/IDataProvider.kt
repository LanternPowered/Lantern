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

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

interface IDataProvider<V : Value<E>, E : Any> : DataProvider<V, E> {

    override fun getKey(): Key<V>

    override fun isSupported(container: DataHolder): Boolean

    override fun allowsAsynchronousAccess(container: DataHolder): Boolean

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

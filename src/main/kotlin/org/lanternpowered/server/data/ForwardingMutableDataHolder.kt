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

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.CollectionValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer

interface ForwardingMutableDataHolder : ForwardingDataHolder, DataHolder.Mutable {

    override val delegateDataHolder: DataHolder.Mutable

    @JvmDefault
    override fun validateRawData(container: DataView): Boolean {
        return this.delegateDataHolder.validateRawData(container)
    }

    @JvmDefault
    override fun setRawData(container: DataView) {
        this.delegateDataHolder.setRawData(container)
    }

    @JvmDefault
    override fun copy(): ValueContainer = super.copy()

    @JvmDefault
    override fun <E> offer(key: Key<out Value<E>>, value: E): DataTransactionResult =
            this.delegateDataHolder.offer(key, value)

    @JvmDefault
    override fun offerAll(value: CollectionValue<*, *>): DataTransactionResult =
            this.delegateDataHolder.offerAll(value)

    @JvmDefault
    override fun offerAll(value: MapValue<*, *>): DataTransactionResult =
            this.delegateDataHolder.offerAll(value)

    @JvmDefault
    override fun <E : Any> offerAll(key: Key<out CollectionValue<E, *>>, elements: Collection<E>): DataTransactionResult =
            this.delegateDataHolder.offerAll(key, elements)

    @JvmDefault
    override fun <K : Any, V : Any> offerAll(key: Key<out MapValue<K, V>>, map: Map<out K, V>): DataTransactionResult =
            this.delegateDataHolder.offerAll(key, map)

    @JvmDefault
    override fun <E : Any> offerSingle(key: Key<out CollectionValue<E, *>>, element: E): DataTransactionResult =
            this.delegateDataHolder.offerSingle(key, element)

    @JvmDefault
    override fun <K : Any, V : Any> offerSingle(key: Key<out MapValue<K, V>>, valueKey: K, value: V): DataTransactionResult =
            this.delegateDataHolder.offerSingle(key, valueKey, value)

    @JvmDefault
    override fun <E : Any> offer(value: Value<E>): DataTransactionResult =
            this.delegateDataHolder.offer(value)

    @JvmDefault
    override fun <E : Any> tryOffer(key: Key<out Value<E>>, value: E): DataTransactionResult =
            this.delegateDataHolder.tryOffer(key, value)

    @JvmDefault
    override fun <E : Any> tryOffer(value: Value<E>): DataTransactionResult =
            this.delegateDataHolder.tryOffer(value)

    @JvmDefault
    override fun remove(key: Key<*>): DataTransactionResult =
            this.delegateDataHolder.remove(key)

    @JvmDefault
    override fun remove(value: Value<*>): DataTransactionResult =
            this.delegateDataHolder.remove(value)

    @JvmDefault
    override fun removeAll(value: CollectionValue<*, *>): DataTransactionResult =
            this.delegateDataHolder.removeAll(value)

    @JvmDefault
    override fun <E : Any> removeAll(key: Key<out CollectionValue<E, *>>, elements: MutableCollection<out E>): DataTransactionResult =
            this.delegateDataHolder.removeAll(key, elements)

    @JvmDefault
    override fun <K : Any, V : Any> removeAll(key: Key<out MapValue<K, V>>, map: Map<out K, V>): DataTransactionResult =
            this.delegateDataHolder.removeAll(key, map)

    @JvmDefault
    override fun <K : Any> removeKey(key: Key<out MapValue<K, *>>, mapKey: K): DataTransactionResult =
            this.delegateDataHolder.removeKey(key, mapKey)

    @JvmDefault
    override fun <E : Any> removeSingle(key: Key<out CollectionValue<E, *>>, element: E): DataTransactionResult =
            this.delegateDataHolder.removeSingle(key, element)

    @JvmDefault
    override fun removeAll(value: MapValue<*, *>): DataTransactionResult =
            this.delegateDataHolder.removeAll(value)

    @JvmDefault
    override fun undo(result: DataTransactionResult): DataTransactionResult =
            this.delegateDataHolder.undo(result)

    @JvmDefault
    override fun copyFrom(that: ValueContainer, function: MergeFunction): DataTransactionResult =
            this.delegateDataHolder.copyFrom(that, function)

    @JvmDefault
    override fun copyFrom(that: ValueContainer): DataTransactionResult =
            this.delegateDataHolder.copyFrom(that)
}

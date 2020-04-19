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
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.CollectionValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer

interface MutableForwardingDataHolder : ForwardingDataHolder, DataHolder.Mutable {

    override val delegateDataHolder: DataHolder.Mutable

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
    override fun offer(value: Value<*>): DataTransactionResult =
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

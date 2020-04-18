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

import org.lanternpowered.api.util.collections.mutableCollectionOf
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.collections.removeAll
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.CollectionValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import java.util.Optional
import java.util.function.Function
import kotlin.collections.set
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
interface MutableDataHolder : DataHolderBase, DataHolder.Mutable {

    /**
     * Gets a element delegate for the given [Key].
     */
    @JvmDefault
    override operator fun <V : Value<E>, E : Any, H : DataHolder> Key<V>.provideDelegate(thisRef: H, property: KProperty<*>):
            MutableDataHolderProperty<H, E> = MutableKeyElementProperty(this)

    /**
     * Gets a element delegate for the given [Key].
     */
    @JvmDefault
    override operator fun <V : Value<E>, E : Any, H : DataHolder> Optional<out Key<V>>.provideDelegate(thisRef: H, property: KProperty<*>):
            MutableDataHolderProperty<H, E> = get().provideDelegate(thisRef, property)

    /**
     * Gets a value delegate for the given [Key].
     */
    @JvmDefault
    override fun <V : Value<E>, E : Any, H : DataHolder> value(key: Key<V>):
            MutableDataHolderProperty<H, V> = MutableKeyValueProperty(key)

    /**
     * Gets a value delegate for the given [Key].
     */
    @JvmDefault
    override fun <V : Value<E>, E : Any, H : DataHolder> value(key: Optional<out Key<V>>):
            MutableDataHolderProperty<H, V> = value(key.get())

    /**
     * Gets a optional element delegate for the given [Key].
     */
    @JvmDefault
    override fun <V : Value<E>, E : Any, H : DataHolder> optional(key: Key<V>):
            MutableDataHolderProperty<H, E?> = MutableKeyOptionalElementProperty(key)

    /**
     * Gets a optional element delegate for the given [Key].
     */
    @JvmDefault
    override fun <V : Value<E>, E : Any, H : DataHolder> optional(key: Optional<out Key<V>>):
            MutableDataHolderProperty<H, E?> = optional(key.get())

    /**
     * Gets a optional value delegate for the given [Key].
     */
    @JvmDefault
    override fun <V : Value<E>, E : Any, H : DataHolder> optionalValue(key: Key<V>):
            MutableDataHolderProperty<H, V?> = MutableKeyOptionalValueProperty(key)

    /**
     * Gets a optional value delegate for the given [Key].
     */
    @JvmDefault
    override fun <V : Value<E>, E : Any, H : DataHolder> optionalValue(key: Optional<out Key<V>>):
            MutableDataHolderProperty<H, V?> = optionalValue(key.get())

    /**
     * A fast equivalent of [transform] which
     * avoids the construction of [DataTransactionResult]s.
     *
     * @param key The key
     * @param function The function
     * @param <E> The element type
     * @return Whether the offer was successful
     */
    @JvmDefault
    fun <E : Any> transformFast(key: Key<out Value<E>>, function: Function<E, E>): Boolean {
        return get(key).map { value -> offerFast(key, value) }.orElse(false)
    }

    /**
     * A fast equivalent of [offer] which avoids the construction of
     * [DataTransactionResult]s.
     *
     * @param key The key
     * @param element The element
     * @return Whether the offer was successful
     */
    @JvmDefault
    fun <E : Any> offerFast(key: Key<out Value<E>>, element: E): Boolean {
        return MutableDataHolderHelper.offerFast(this, key, element)
    }

    /**
     * A fast equivalent of [offer] which avoids the construction of
     * [DataTransactionResult]s and doesn't post any value change events.
     *
     * @param key The key
     * @param element The element
     * @return Whether the offer was successful
     */
    @JvmDefault
    fun <E : Any> offerFastNoEvents(key: Key<out Value<E>>, element: E): Boolean {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<E>, E>().offerFast(this, element)
        }

        return false
    }

    @JvmDefault
    override fun <E : Any> offer(key: Key<out Value<E>>, element: E): DataTransactionResult {
        return MutableDataHolderHelper.offer(this, key, element)
    }

    @JvmDefault
    fun <E : Any> offerNoEvents(key: Key<out Value<E>>, element: E): DataTransactionResult {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<E>, E>().offer(this, element)
        }

        return DataTransactionResult.failNoData()
    }

    /**
     * A fast equivalent of [offer] which avoids the construction of
     * [DataTransactionResult]s.
     *
     * @param value The value
     * @return Whether the offer was successful
     */
    @JvmDefault
    fun <E : Any> offerFast(value: Value<E>): Boolean {
        return MutableDataHolderHelper.offerFast(this, value)
    }

    @JvmDefault
    fun <E : Any> offerFastNoEvents(value: Value<E>): Boolean {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[value.key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<E>, E>().offerValueFast(this, value)
        }

        return false
    }

    @JvmDefault
    override fun offer(value: Value<*>): DataTransactionResult {
        return MutableDataHolderHelper.offer(this, value)
    }

    @JvmDefault
    fun <E : Any> offerNoEvents(value: Value<E>): DataTransactionResult {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[value.key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<E>, E>().offerValue(this, value)
        }

        return DataTransactionResult.failNoData()
    }

    @JvmDefault
    override fun offerAll(value: CollectionValue<*, *>): DataTransactionResult {
        return offerCollectionValues(value.uncheckedCast<CollectionValue<Any, MutableCollection<Any>>>())
    }

    @JvmDefault
    private fun <E : Any, C : MutableCollection<E>> offerCollectionValues(value: CollectionValue<E, C>): DataTransactionResult {
        return offer(value.key, get(value.key).map { collection -> collection.apply { addAll(value.all) } }.orElseGet { value.all })
    }

    @JvmDefault
    override fun <E : Any> offerAll(key: Key<out CollectionValue<E, *>>, elements: MutableCollection<out E>): DataTransactionResult {
        return offerCollectionValues(key.uncheckedCast(), elements.uncheckedCast<MutableCollection<Any>>())
    }

    @JvmDefault
    private fun <E : Any, C : MutableCollection<E>> offerCollectionValues(key: Key<out CollectionValue<E, C>>, elements: C): DataTransactionResult {
        return offer(key, get(key).map { collection -> collection.apply { addAll(elements) } }.orElseGet { elements })
    }

    @JvmDefault
    override fun offerAll(value: MapValue<*, *>): DataTransactionResult {
        return offerMapEntries(value.uncheckedCast<MapValue<Any, Any>>())
    }

    @JvmDefault
    private fun <K : Any, V : Any> offerMapEntries(value: MapValue<K, V>): DataTransactionResult {
        return offer(value.key, get(value.key).map { collection -> collection.apply { putAll(value.get()) } }.orElseGet { value.get() })
    }

    @JvmDefault
    override fun <K : Any, V : Any> offerAll(key: Key<out MapValue<K, V>>, map: MutableMap<out K, out V>): DataTransactionResult {
        return offer(key, get(key).map { collection -> collection.apply { putAll(map) } }.orElseGet { map.uncheckedCast() })
    }

    @JvmDefault
    override fun <E : Any> offerSingle(key: Key<out CollectionValue<E, *>>, element: E): DataTransactionResult {
        return offerCollectionValue(key.uncheckedCast<Key<CollectionValue<Any, MutableCollection<Any>>>>(), element)
    }

    @JvmDefault
    private fun <E : Any, C : MutableCollection<E>> offerCollectionValue(key: Key<out CollectionValue<E, C>>, element: E): DataTransactionResult {
        return offer(key, get(key)
                .map { collection -> collection.apply { add(element) } }
                .orElseGet { mutableCollectionOf(key.elementToken.rawType.uncheckedCast<Class<C>>()) }.apply { add(element) })
    }

    @JvmDefault
    override fun <K : Any, V : Any> offerSingle(key: Key<out MapValue<K, V>>, valueKey: K, value: V): DataTransactionResult {
        val map: MutableMap<K, V> = get(key).orElseGet { HashMap() }
        map[valueKey] = value
        return offer(key, map)
    }

    /**
     * A fast equivalent of [tryOffer] which avoids the
     * construction of [DataTransactionResult]s.
     *
     * @param key The key
     * @param value The value
     * @return Whether the offer was successful
     */
    @JvmDefault
    fun <E : Any> tryOfferFast(key: Key<out Value<E>>, value: E): Boolean {
        val result = offerFast(key, value)
        if (!result) {
            throw IllegalArgumentException("Failed offer transaction!")
        }
        return true
    }

    /**
     * A fast equivalent of [tryOffer] which
     * avoids the construction of [DataTransactionResult]s.
     *
     * @param value The value to offer
     * @return Whether the offer was successful
     */
    @JvmDefault
    fun <E : Any> tryOfferFast(value: Value<E>): Boolean {
        return tryOfferFast(value.key, value.get())
    }

    @JvmDefault
    override fun <E : Any> tryOffer(key: Key<out Value<E>>, value: E): DataTransactionResult {
        val result = offer(key, value)
        if (!result.isSuccessful) {
            throw IllegalArgumentException("Failed offer transaction!")
        }
        return result
    }

    /**
     * A fast equivalent of [remove] which
     * avoids the construction of [DataTransactionResult]s.
     *
     * @param key The key to remove
     * @return Whether the removal was successful
     */
    @JvmDefault
    fun removeFast(key: Key<*>): Boolean {
        return MutableDataHolderHelper.removeFast(this, key)
    }

    /**
     * A fast equivalent of [remove] which avoids the construction of
     * [DataTransactionResult]s and doesn't throw value change events.
     *
     * @param key The key to remove
     * @return Whether the removal was successful
     */
    @JvmDefault
    fun removeFastNoEvents(key: Key<*>): Boolean {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (globalRegistration != null) {
            return globalRegistration.anyDataProvider().removeFast(this)
        }

        return false
    }

    @JvmDefault
    override fun remove(key: Key<*>): DataTransactionResult {
        return MutableDataHolderHelper.remove(this, key)
    }

    @JvmDefault
    fun removeNoEvents(key: Key<*>): DataTransactionResult {
        // Check for a global registration
        val globalRegistration = GlobalKeyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (globalRegistration != null) {
            return globalRegistration.anyDataProvider().remove(this)
        }

        return DataTransactionResult.failNoData()
    }

    /**
     * A fast equivalent of [remove] which
     * avoids the construction of [DataTransactionResult]s.
     *
     * @param value The value
     * @return Whether the removal was successful
     */
    @JvmDefault
    fun removeFast(value: Value<*>): Boolean {
        return removeFast(value.key)
    }

    @JvmDefault
    override fun removeAll(value: CollectionValue<*, *>): DataTransactionResult {
        return removeCollectionValues(value.uncheckedCast<CollectionValue<Any, MutableCollection<Any>>>())
    }

    @JvmDefault
    private fun <E : Any, C : MutableCollection<E>> removeCollectionValues(value: CollectionValue<E, C>): DataTransactionResult {
        return get(value.key).map { collection ->
            collection.removeAll(value.all)
            offer(value.key, collection)
        }.orElseGet { DataTransactionResult.failNoData() }
    }

    @JvmDefault
    override fun <E : Any> removeAll(key: Key<out CollectionValue<E, *>>, elements: Collection<E>): DataTransactionResult {
        return removeCollectionValues(key.uncheckedCast(), elements)
    }

    @JvmDefault
    private fun <E : Any, C : MutableCollection<E>> removeCollectionValues(
            key: Key<out CollectionValue<E, C>>, elements: Collection<E>): DataTransactionResult {
        return get(key).map { collection ->
            collection.removeAll(elements)
            offer(key, collection)
        }.orElseGet { DataTransactionResult.failNoData() }
    }

    @JvmDefault
    override fun removeAll(value: MapValue<*, *>): DataTransactionResult {
        return removeMapEntries(value.uncheckedCast<MapValue<Any, Any>>())
    }

    @JvmDefault
    private fun <K : Any, V : Any> removeMapEntries(value: MapValue<K, V>) = removeAll(value.key, value.get())

    @JvmDefault
    override fun <K : Any, V : Any> removeAll(key: Key<out MapValue<K, V>>, map: MutableMap<out K, out V>): DataTransactionResult {
        return get(key).map { storedMap ->
            storedMap.removeAll(map)
            offer(key, storedMap)
        }.orElseGet { DataTransactionResult.failNoData() }
    }

    @JvmDefault
    override fun <K : Any> removeKey(key: Key<out MapValue<K, *>>, mapKey: K): DataTransactionResult {
        return removeMapKey(key.uncheckedCast<Key<MapValue<K, Any>>>(), mapKey)
    }

    @JvmDefault
    private fun <K : Any, V : Any> removeMapKey(key: Key<out MapValue<K, V>>, mapKey: K): DataTransactionResult {
        return get(key).map { storedMap ->
            storedMap.remove(mapKey)
            offer(key, storedMap)
        }.orElseGet { DataTransactionResult.failNoData() }
    }

    @JvmDefault
    override fun <E : Any> removeSingle(key: Key<out CollectionValue<E, *>>, element: E): DataTransactionResult {
        return removeCollectionValue(key.uncheckedCast(), element)
    }

    @JvmDefault
    private fun <E : Any, C : MutableCollection<E>> removeCollectionValue(key: Key<out CollectionValue<E, C>>, value: E): DataTransactionResult {
        return get(key).map { collection ->
            collection.remove(value)
            offer(key, collection)
        }.orElseGet { DataTransactionResult.failNoData() }
    }

    @JvmDefault
    override fun remove(value: Value<*>): DataTransactionResult {
        return super.remove(value)
    }

    /**
     * A fast equivalent of [undo] which avoids the construction of
     * [DataTransactionResult]s.
     *
     * @param result The result
     * @return Whether the undo was successful
     */
    @JvmDefault
    fun undoFast(result: DataTransactionResult): Boolean {
        return MutableDataHolderHelper.undoFast(this, result)
    }

    /**
     * A fast equivalent of [undo] which avoids the construction of
     * [DataTransactionResult]s and doesn't throw value change events.
     *
     * @param result The result
     * @return Whether the undo was successful
     */
    @JvmDefault
    fun undoFastNoEvents(result: DataTransactionResult): Boolean {
        if (result.replacedData.isEmpty() && result.successfulData.isEmpty()) {
            return true
        }
        result.successfulData.forEach { value -> removeFastNoEvents(value.key) }
        result.replacedData.forEach { value -> offerFastNoEvents(value) }
        return true
    }

    @JvmDefault
    override fun undo(result: DataTransactionResult): DataTransactionResult {
        return MutableDataHolderHelper.undo(this, result)
    }

    /**
     * Similar to [undo], but doesn't throw value change events.
     *
     * @param result The result
     * @return Whether the undo was successful
     */
    @JvmDefault
    fun undoNoEvents(result: DataTransactionResult): DataTransactionResult {
        if (result.replacedData.isEmpty() && result.successfulData.isEmpty()) {
            return DataTransactionResult.successNoData()
        }
        val builder = DataTransactionResult.builder()
        for (replaced in result.replacedData) {
            builder.absorbResult(offerNoEvents(replaced))
        }
        for (successful in result.successfulData) {
            builder.absorbResult(removeNoEvents(successful.key))
        }
        return builder.result(DataTransactionResult.Type.SUCCESS).build()
    }

    @JvmDefault
    override fun copyFrom(that: ValueContainer, function: MergeFunction): DataTransactionResult {
        return MutableDataHolderHelper.copyFrom(this, that, function)
    }

    @JvmDefault
    fun copyFromNoEvents(that: ValueContainer, function: MergeFunction): DataTransactionResult {
        val builder = DataTransactionResult.builder()
        var success = false
        if (function === MergeFunction.REPLACEMENT_PREFERRED) {
            // Lets boost performance a bit by avoiding
            // constructing unnecessary values
            for (key in that.keys) {
                val theKey = key.uncheckedCast<Key<Value<Any>>>()
                val replacement = that.get(theKey).orNull()
                if (replacement != null) {
                    val result = offerNoEvents(theKey, replacement)
                    builder.absorbResult(result)
                    if (result.isSuccessful) {
                        success = true
                    }
                }
            }
        } else {
            for (key in that.keys) {
                val theKey = key.uncheckedCast<Key<Value<Any>>>()
                var replacement = that.getValue(theKey).orNull()
                if (replacement != null) {
                    val original = getValue(theKey).orNull()
                    replacement = function.merge(original, replacement)
                    val result = offerNoEvents(theKey, replacement)
                    builder.absorbResult(result)
                    if (result.isSuccessful) {
                        success = true
                    }
                }
            }
        }
        return builder.result(if (success) DataTransactionResult.Type.SUCCESS else DataTransactionResult.Type.FAILURE).build()
    }

    @JvmDefault
    fun copyFromFast(that: ValueContainer): Boolean {
        return copyFromFast(that, MergeFunction.REPLACEMENT_PREFERRED)
    }

    @JvmDefault
    fun copyFromFast(that: ValueContainer, function: MergeFunction): Boolean {
        return MutableDataHolderHelper.copyFromFast(this, that, function)
    }

    @JvmDefault
    fun copyFromFastNoEvents(that: ValueContainer): Boolean {
        return copyFromFastNoEvents(that, MergeFunction.REPLACEMENT_PREFERRED)
    }

    @JvmDefault
    fun copyFromFastNoEvents(that: ValueContainer, function: MergeFunction): Boolean {
        var success = false
        if (function === MergeFunction.REPLACEMENT_PREFERRED) {
            // Lets boost performance a bit by avoiding
            // constructing unnecessary values
            for (key in that.keys) {
                val theKey = key.uncheckedCast<Key<Value<Any>>>()
                val replacement = that.get(theKey).orNull()
                if (replacement != null) {
                    if (offerFastNoEvents(theKey, replacement)) {
                        success = true
                    }
                }
            }
        } else {
            for (key in that.keys) {
                val theKey = key.uncheckedCast<Key<Value<Any>>>()
                var replacement = that.getValue(theKey).orNull()
                if (replacement != null) {
                    val original = getValue(theKey).orNull()
                    replacement = function.merge(original, replacement)
                    if (offerFastNoEvents(theKey, replacement)) {
                        success = true
                    }
                }
            }
        }
        return success
    }
}

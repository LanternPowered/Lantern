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

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.server.data.key.ValueKey
import org.lanternpowered.server.data.key.ValueKeyEventListener
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer

object MutableDataHolderHelper {

    private fun getKeys(result: DataTransactionResult): Set<Key<*>> {
        val keys = mutableSetOf<Key<*>>()
        result.replacedData.forEach { value -> keys.add(value.key) }
        result.successfulData.forEach { value -> keys.add(value.key) }
        // We don't need the rejected keys, they didn't modify any values
        return keys
    }

    private fun processDataTransactionResult(
            holder: MutableDataHolder, transactionResult: DataTransactionResult, hasListeners: () -> Boolean = { true }
    ): DataTransactionResult {
        var result = transactionResult
        if (!result.isSuccessful || !hasListeners()) {
            return result
        }
        val cause = CauseStack.currentOrEmpty().currentCause
        val event = LanternEventFactory.createChangeDataHolderEventValueChange(cause, result, holder)
        Lantern.eventManager.post(event)
        // Nothing is allowed to change, revert everything fast
        if (event.isCancelled) {
            holder.undoFastNoEvents(result)
            return DataTransactionResult.failNoData()
        }
        val original = result
        result = event.endResult
        // Check if something actually changed
        if (result !== original) {
            val success = mutableMapOf<Key<*>, Value.Immutable<*>>()
            for (value in original.successfulData) {
                success[value.key] = value
            }
            for (value in result.successfulData) {
                val value1 = success.remove(value.key)
                if (value1 == null || value1.get() !== value.get()) {
                    holder.offerNoEvents(value)
                }
            }
            // A previously successful offering got removed, revert this
            if (success.isNotEmpty()) {
                for (value in original.replacedData) {
                    if (value.key in success) {
                        holder.offerNoEvents(value)
                    }
                }
            }
        }
        return event.endResult
    }

    private fun hasListeners(store: MutableDataHolder, key: Key<*>): Boolean {
        return hasListeners(store, setOf(key))
    }

    private fun hasListeners(store: MutableDataHolder, keys: Iterable<Key<*>>): Boolean {
        for (key in keys) {
            val dataHolder = store as DataHolder
            val listeners = (key as ValueKey<*,*>).listeners
            for (listener in listeners) {
                if ((listener.listener as ValueKeyEventListener).dataHolderFilter(dataHolder)) {
                    return true
                }
            }
        }
        return false
    }

    fun <V : Value<E>, E : Any> offerFast(store: MutableDataHolder, key: Key<V>, element: E): Boolean {
        val hasListeners = hasListeners(store, key)
        return if (hasListeners) offer(store, key, element).isSuccessful else store.offerFastNoEvents(key, element)
    }

    fun <V : Value<E>, E : Any> offer(store: MutableDataHolder, key: Key<V>, element: E): DataTransactionResult {
        return offer(store, key, element) { hasListeners(store, key) }
    }

    fun <V : Value<E>, E : Any> offer(store: MutableDataHolder, key: Key<V>, element: E, hasListeners: () -> Boolean): DataTransactionResult {
        return processDataTransactionResult(store, store.offerNoEvents(key, element), hasListeners)
    }

    fun <E : Any> offerFast(store: MutableDataHolder, value: Value<E>): Boolean {
        val hasListeners = hasListeners(store, value.key)
        return if (hasListeners) offer(store, value).isSuccessful else store.offerFastNoEvents(value)
    }

    fun <E : Any> offer(store: MutableDataHolder, value: Value<E>): DataTransactionResult {
        return offer(store, value) { hasListeners(store, value.key) }
    }

    fun <E : Any> offer(store: MutableDataHolder, value: Value<E>, hasListeners: () -> Boolean): DataTransactionResult {
        return processDataTransactionResult(store, store.offerNoEvents(value), hasListeners)
    }

    fun removeFast(store: MutableDataHolder, key: Key<*>): Boolean {
        val hasListeners = hasListeners(store, key)
        return if (hasListeners) remove(store, key).isSuccessful else store.removeFastNoEvents(key)
    }

    fun remove(store: MutableDataHolder, key: Key<*>): DataTransactionResult {
        return remove(store, key) { hasListeners(store, key) }
    }

    fun remove(store: MutableDataHolder, key: Key<*>, hasListeners: () -> Boolean): DataTransactionResult {
        return processDataTransactionResult(store, store.removeNoEvents(key), hasListeners)
    }

    fun undoFast(store: MutableDataHolder, result: DataTransactionResult): Boolean {
        val hasListeners = hasListeners(store, getKeys(result))
        return if (hasListeners) undo(store, result).isSuccessful else store.undoFastNoEvents(result)
    }

    fun undo(store: MutableDataHolder, result: DataTransactionResult): DataTransactionResult {
        return undo(store, result) { hasListeners(store, getKeys(result)) }
    }

    fun undo(store: MutableDataHolder, result: DataTransactionResult, hasListeners: () -> Boolean): DataTransactionResult {
        return processDataTransactionResult(store, store.undoNoEvents(result), hasListeners)
    }

    fun copyFrom(store: MutableDataHolder, that: ValueContainer, function: MergeFunction): DataTransactionResult {
        return processDataTransactionResult(store, store.copyFromNoEvents(that, function))
    }

    fun copyFromFast(store: MutableDataHolder, that: ValueContainer, function: MergeFunction): Boolean {
        return processDataTransactionResult(store, store.copyFromNoEvents(that, function)).isSuccessful
    }
}

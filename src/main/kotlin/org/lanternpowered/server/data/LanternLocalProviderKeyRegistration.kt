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

import org.lanternpowered.api.util.optional.orNull
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

@Suppress("UNCHECKED_CAST")
internal class LanternLocalProviderKeyRegistration<V : Value<E>, E : Any, H : DataHolder>(
        key: Key<V>, private val provider: IDataProvider<V, E>
) : LanternLocalKeyRegistration<V, E, H>(key) {

    override val dataProvider = object : IDataProvider<V, E> {

        private fun postChanges(container: DataHolder, listeners: Iterable<H.(newValue: E?, oldValue: E?) -> Unit>, original: E?) {
            val replacement = get(container).orNull()
            if (replacement != original) {
                listeners.forEach { listener -> listener(container as H, replacement, original) }
            }
        }

        override fun offerValueFast(container: DataHolder.Mutable, value: V): Boolean {
            changeListeners?.also { listeners ->
                val original = get(container).orNull()
                val success = provider.offerValueFast(container, value)
                if (success) {
                    postChanges(container, listeners, original)
                }
                return success
            }
            return provider.offerValueFast(container, value)
        }

        override fun offerValue(container: DataHolder.Mutable, value: V): DataTransactionResult {
            changeListeners?.also { listeners ->
                val original = get(container).orNull()
                val result = provider.offerValue(container, value)
                if (result.isSuccessful) {
                    postChanges(container, listeners, original)
                }
                return result
            }
            return provider.offerValue(container, value)
        }

        override fun offerFast(container: DataHolder.Mutable, element: E): Boolean {
            changeListeners?.also { listeners ->
                val original = get(container).orNull()
                val success = provider.offerFast(container, element)
                if (success) {
                    postChanges(container, listeners, original)
                }
                return success
            }
            return provider.offerFast(container, element)
        }

        override fun offer(container: DataHolder.Mutable, element: E): DataTransactionResult {
            changeListeners?.also { listeners ->
                val original = get(container).orNull()
                val result = provider.offer(container, element)
                if (result.isSuccessful) {
                    postChanges(container, listeners, original)
                }
                return result
            }
            return provider.offer(container, element)
        }

        override fun remove(container: DataHolder.Mutable): DataTransactionResult {
            changeListeners?.also { listeners ->
                val original = get(container).orNull()
                val result = provider.remove(container)
                if (result.isSuccessful) {
                    postChanges(container, listeners, original)
                }
                return result
            }
            return provider.remove(container)
        }

        override fun removeFast(container: DataHolder.Mutable): Boolean {
            changeListeners?.also { listeners ->
                val original = get(container).orNull()
                val success = provider.removeFast(container)
                if (success) {
                    postChanges(container, listeners, original)
                }
                return success
            }
            return provider.removeFast(container)
        }

        override fun allowsAsynchronousAccess(container: DataHolder) = provider.allowsAsynchronousAccess(container)

        override fun get(container: DataHolder) = provider.get(container)

        override fun getValue(container: DataHolder) = provider.getValue(container)

        override fun getKey() = provider.key

        override fun isSupported(container: DataHolder) = provider.isSupported(container)

        override fun <I : DataHolder.Immutable<I>> with(immutable: I, element: E) = provider.with(immutable, element)

        override fun <I : DataHolder.Immutable<I>> withValue(immutable: I, value: V) = provider.withValue(immutable, value)

        override fun <I : DataHolder.Immutable<I>> without(immutable: I) = provider.without(immutable)
    }

    override fun copy(): LanternLocalKeyRegistration<V, E, H> {
        val copy = LanternLocalProviderKeyRegistration<V, E, H>(this.key, this.provider)
        this.changeListeners?.let { copy.changeListeners = ArrayList(it) }
        return copy
    }
}

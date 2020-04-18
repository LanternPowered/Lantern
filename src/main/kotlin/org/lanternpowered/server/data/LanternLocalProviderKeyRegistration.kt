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

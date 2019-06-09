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
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

internal class LanternGlobalKeyRegistration<V : Value<E>, E : Any>(key: Key<V>) :
        LanternKeyRegistration<V, E>(key), GlobalKeyRegistration<V, E> {

    private val providers = mutableListOf<DataProvider<V, E>>()

    override val dataProvider = object : IDataProvider<V, E> {

        override fun offerValue(container: DataHolder.Mutable, value: V): DataTransactionResult {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return provider.offerValue(container, value)
                }
            }
            return DataTransactionResult.failResult(value.asImmutable())
        }

        override fun offerValueFast(container: DataHolder.Mutable, value: V): Boolean {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return if (provider is IDataProvider<V, E>) {
                        provider.offerValueFast(container, value)
                    } else {
                        provider.offerValue(container, value).isSuccessful
                    }
                }
            }
            return false
        }

        override fun offer(container: DataHolder.Mutable, element: E): DataTransactionResult {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return provider.offer(container, element)
                }
            }
            return DataTransactionResult.failResult(ValueFactory.immutableOf(key, element).asImmutable())
        }

        override fun offerFast(container: DataHolder.Mutable, element: E): Boolean {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return if (provider is IDataProvider<V, E>) {
                        provider.offerFast(container, element)
                    } else {
                        provider.offer(container, element).isSuccessful
                    }
                }
            }
            return false
        }

        override fun allowsAsynchronousAccess(container: DataHolder): Boolean {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return if (provider is IDataProvider<V, E>) {
                        provider.allowsAsynchronousAccess(container)
                    } else false
                }
            }
            return false
        }

        // This method cannot be implemented properly without knowing the container
        override fun allowsAsynchronousAccess(token: TypeToken<out DataHolder>) = false

        override fun get(container: DataHolder): Optional<E> {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return provider.get(container)
                }
            }
            return emptyOptional()
        }

        override fun getKey() = this@LanternGlobalKeyRegistration.key

        override fun isSupported(container: DataHolder): Boolean {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return provider.isSupported(container)
                }
            }
            return false
        }

        override fun remove(container: DataHolder.Mutable): DataTransactionResult {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return provider.remove(container)
                }
            }
            return DataTransactionResult.failNoData()
        }

        override fun removeFast(container: DataHolder.Mutable): Boolean {
            for (provider in providers) {
                if (provider.isSupported(container)) {
                    return if (provider is IDataProvider<V, E>) {
                        provider.removeFast(container)
                    } else {
                        provider.remove(container).isSuccessful
                    }
                }
            }
            return false
        }

        override fun <I : DataHolder.Immutable<I>> with(immutable: I, element: E): Optional<I> {
            for (provider in providers) {
                if (provider.isSupported(immutable)) {
                    return provider.with(immutable, element)
                }
            }
            return emptyOptional()
        }

        override fun <I : DataHolder.Immutable<I>> without(immutable: I): Optional<I> {
            for (provider in providers) {
                if (provider.isSupported(immutable)) {
                    return provider.without(immutable)
                }
            }
            return emptyOptional()
        }
    }

    override fun addProvider(provider: DataProvider<V, E>) = apply {
        this.providers.add(provider)
    }

    override fun addProvider(fn: DataProviderBuilder<V, E>.(key: Key<V>) -> Unit) = apply {
        addProvider(DataProviderBuilder.of(this.key, fn).build())
    }
}

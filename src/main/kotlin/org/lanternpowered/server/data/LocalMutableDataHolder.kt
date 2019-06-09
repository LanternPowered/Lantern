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

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

/**
 * The base class of all the mutable [DataHolder]s.
 */
interface LocalMutableDataHolder : LocalDataHolder, MutableDataHolder {

    override val keyRegistry: LocalKeyRegistry<out LocalMutableDataHolder>

    @JvmDefault
    override fun <E : Any> offerFastNoEvents(key: Key<out Value<E>>, element: E): Boolean {
        // Check the local key registration
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<E>, E>().offerFast(this, element)
        }

        if (super.offerFastNoEvents(key, element)) {
            return true
        }

        key as ValueKey<*,*>
        // Implicitly register the key locally
        if (!key.requiresExplicitRegistration) {
            this.keyRegistry.register(key, element).removable()
        }

        return false
    }

    @JvmDefault
    override fun <E : Any> offerNoEvents(key: Key<out Value<E>>, element: E): DataTransactionResult {
        // Check the local key registration
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<E>, E>().offer(this, element)
        }

        val result = super.offerNoEvents(key, element)
        if (result.isSuccessful) {
            return result
        }

        key as ValueKey<*,*>
        // Implicitly register the key locally
        if (!key.requiresExplicitRegistration) {
            this.keyRegistry.register(key, element).removable()
        }

        return DataTransactionResult.failNoData()
    }

    @JvmDefault
    override fun <E : Any> offerFastNoEvents(value: Value<E>): Boolean {
        val key = value.key

        // Check the local key registration
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<E>, E>().offerValueFast(this, value)
        }

        if (super.offerFastNoEvents(value)) {
            return true
        }

        key as ValueKey<*,*>
        // Implicitly register the key locally
        if (!key.requiresExplicitRegistration) {
            this.keyRegistry.register(key, value.get()).removable()
        }

        return false
    }

    @JvmDefault
    override fun <E : Any> offerNoEvents(value: Value<E>): DataTransactionResult {
        val key = value.key

        // Check the local key registration
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<E>, E>().offerValue(this, value)
        }

        val result = super.offerNoEvents(value)
        if (result.isSuccessful) {
            return result
        }

        key as ValueKey<*,*>
        // Implicitly register the key locally
        if (!key.requiresExplicitRegistration) {
            this.keyRegistry.register(key, value.get()).removable()
        }

        return DataTransactionResult.failNoData()
    }

    @JvmDefault
    override fun removeFastNoEvents(key: Key<*>): Boolean {
        // Check the local key registration
        val localRegistration = this.keyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (localRegistration != null) {
            return localRegistration.anyDataProvider().removeFast(this)
        }

        return super.removeFastNoEvents(key)
    }

    @JvmDefault
    override fun removeNoEvents(key: Key<*>): DataTransactionResult {
        // Check the local key registration
        val localRegistration = this.keyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (localRegistration != null) {
            return localRegistration.anyDataProvider().remove(this)
        }

        return super.removeNoEvents(key)
    }
}

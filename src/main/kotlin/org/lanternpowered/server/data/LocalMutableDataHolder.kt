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

import org.lanternpowered.api.util.uncheckedCast
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

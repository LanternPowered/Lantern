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

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

interface LocalDataHolder : ValueContainerBase, DataHolderBase {

    /**
     * Gets the [LocalKeyRegistry].
     *
     * @return The key registry
     */
    val keyRegistry: LocalKeyRegistry<out LocalDataHolder>

    /**
     * A convenient extension that applies changes as the caller type.
     */
    @JvmDefault
    fun <H : LocalDataHolder> H.keyRegistry(fn: LocalKeyRegistry<H>.() -> Unit): LocalKeyRegistry<H> {
        return this.keyRegistry.forHolderUnchecked<H>().apply(fn)
    }

    @JvmDefault
    override fun supports(key: Key<*>): Boolean = supportsKey(key.uncheckedCast<Key<Value<Any>>>())

    /**
     * Gets whether the [Key] is supported by this [LocalDataHolder].
     */
    @JvmDefault
    private fun <V : Value<E>, E : Any> supportsKey(key: Key<V>): Boolean {
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.anyDataProvider().isSupported(this)
        }
        return super<DataHolderBase>.supports(key)
    }

    @JvmDefault
    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V> {
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.dataProvider<V, E>().getValue(this)
        }
        return super<DataHolderBase>.getValue(key)
    }

    @JvmDefault
    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E> {
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<E>, E>().get(this)
        }
        return super<DataHolderBase>.get(key)
    }

    @JvmDefault
    override fun getKeys(): Set<Key<*>> {
        val keys = ImmutableSet.builder<Key<*>>()
        keys.addAll(this.keyRegistry.keys)
        keys.addAll(super.getKeys())
        return keys.build()
    }

    @JvmDefault
    override fun getValues(): Set<Value.Immutable<*>> {
        val values = ImmutableSet.builder<Value.Immutable<*>>()
        values.addAll(super.getValues())

        for (registration in this.keyRegistry.registrations) {
            registration.anyDataProvider().getValue(this).ifPresent { value -> values.add(value.asImmutable()) }
        }

        return values.build()
    }
}

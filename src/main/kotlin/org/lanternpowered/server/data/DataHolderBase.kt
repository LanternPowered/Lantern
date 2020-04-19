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
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional
import kotlin.reflect.KProperty

interface DataHolderBase : DataHolder, ValueContainerBase {

    /**
     * Gets a element delegate for the given [Key].
     */
    @JvmDefault
    operator fun <V : Value<E>, E : Any, H : DataHolder> Key<V>.provideDelegate(thisRef: H, property: KProperty<*>):
            DataHolderProperty<H, E> = KeyElementProperty(this)

    /**
     * Gets a element delegate for the given [Key].
     */
    @JvmDefault
    operator fun <V : Value<E>, E : Any, H : DataHolder> Optional<out Key<V>>.provideDelegate(thisRef: H, property: KProperty<*>):
            DataHolderProperty<H, E> = get().provideDelegate(thisRef, property)

    /**
     * Gets a value delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> value(key: Key<V>): DataHolderProperty<H, V> = KeyValueProperty(key)

    /**
     * Gets a value delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> value(key: Optional<out Key<V>>): DataHolderProperty<H, V> = value(key.get())

    /**
     * Gets a optional element delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> optional(key: Key<V>): DataHolderProperty<H, E?> = OptionalKeyElementProperty(key)

    /**
     * Gets a optional element delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> optional(key: Optional<out Key<V>>): DataHolderProperty<H, E?> = optional(key.get())

    /**
     * Gets a optional value delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> optionalValue(key: Key<V>): DataHolderProperty<H, V?> = OptionalKeyValueProperty(key)

    /**
     * Gets a optional value delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> optionalValue(key: Optional<out Key<V>>): DataHolderProperty<H, V?> = optionalValue(key.get())

    @JvmDefault
    override fun supports(key: Key<*>) = supportsKey(key.uncheckedCast<Key<Value<Any>>>())

    /**
     * Gets whether the [Key] is supported by this [LocalDataHolder].
     */
    @JvmDefault
    private fun <V : Value<E>, E : Any> supportsKey(key: Key<V>): Boolean {
        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.anyDataProvider().isSupported(this)
        }

        return false
    }

    @JvmDefault
    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V> {
        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<V, E>().getValue(this)
        }

        return emptyOptional()
    }

    @JvmDefault
    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E> {
        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<Value<E>, E>().get(this)
        }

        return emptyOptional()
    }

    @JvmDefault
    override fun getKeys(): Set<Key<*>> {
        val keys = ImmutableSet.builder<Key<*>>()

        GlobalKeyRegistry.registrations.stream()
                .filter { registration -> registration.anyDataProvider().isSupported(this) }
                .forEach { registration -> keys.add(registration.key) }

        return keys.build()
    }

    @JvmDefault
    override fun getValues(): Set<Value.Immutable<*>> {
        val values = ImmutableSet.builder<Value.Immutable<*>>()

        for (registration in GlobalKeyRegistry.registrations) {
            registration.anyDataProvider().getValue(this).ifPresent { value -> values.add(value.asImmutable()) }
        }

        return values.build()
    }
}

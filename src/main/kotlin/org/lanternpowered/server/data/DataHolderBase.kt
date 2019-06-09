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

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.key.OptionalUnwrappedValueKey
import org.lanternpowered.server.data.value.LanternImmutableValue
import org.lanternpowered.server.data.value.LanternMutableValue
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.value.OptionalValue
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
     * Gets a value delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> value(key: Key<V>): DataHolderProperty<H, V> = KeyValueProperty(key)

    /**
     * Gets a optional element delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> optional(key: Key<V>): DataHolderProperty<H, E?> = OptionalKeyElementProperty(key)

    /**
     * Gets a optional value delegate for the given [Key].
     */
    @JvmDefault
    fun <V : Value<E>, E : Any, H : DataHolder> optionalValue(key: Key<V>): DataHolderProperty<H, V?> = OptionalKeyValueProperty(key)

    /**
     * Gets the content version of this data holder. Defaults to `1`.
     *
     * @return The content version
     */
    @JvmDefault
    override fun getContentVersion(): Int = 1

    @JvmDefault
    override fun toContainer(): DataContainer {
        val dataContainer = DataContainer.createNew()
        DataHelper.serializeRawData(dataContainer, this)
        return dataContainer
    }

    @JvmDefault
    override fun supports(key: Key<*>) = supportsKey(key.uncheckedCast<Key<Value<Any>>>())

    /**
     * Gets whether the [Key] is supported by this [LocalDataHolder].
     */
    @JvmDefault
    private fun <V : Value<E>, E : Any> supportsKey(key: Key<V>): Boolean {
        if (key is OptionalUnwrappedValueKey<*, *>) {
            return supports(key.wrappedKey)
        }

        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.anyDataProvider().isSupported(this)
        }

        return false
    }

    /**
     * Gets the [Value] for the unwrapped variant of a [OptionalValue] key.
     */
    @JvmDefault
    private fun <E : Any, V : Value<E>> getOptionalUnwrappedValue(key: OptionalUnwrappedValueKey<V, E>): Optional<V> {
        val optOptionalValue = getValue(key.wrappedKey)
        if (!optOptionalValue.isPresent) {
            return emptyOptional()
        }
        val optionalValue = optOptionalValue.get()
        val optElement = optionalValue.get()
        if (!optElement.isPresent) {
            return emptyOptional()
        }
        val element = optElement.get()
        return if (optionalValue is OptionalValue.Mutable<*>) {
            LanternMutableValue(key, element).uncheckedCast<V>().optional()
        } else {
            LanternImmutableValue.cachedOf(key, element).uncheckedCast<V>().optional()
        }
    }

    @JvmDefault
    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V> {
        if (key is OptionalUnwrappedValueKey<*, *>) {
            return getOptionalUnwrappedValue(key.uncheckedCast())
        }

        val globalRegistration = GlobalKeyRegistry[key]
        if (globalRegistration != null) {
            return globalRegistration.dataProvider<V, E>().getValue(this)
        }

        return emptyOptional()
    }

    /**
     * Gets the element for the unwrapped variant of a [OptionalValue] key.
     */
    @JvmDefault
    private fun <E : Any> getOptionalUnwrappedElement(key: OptionalUnwrappedValueKey<*, E>) = get(key.wrappedKey).orElse(emptyOptional())

    @JvmDefault
    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E> {
        if (key is OptionalUnwrappedValueKey<*, *>) {
            return getOptionalUnwrappedElement(key.uncheckedCast())
        }

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

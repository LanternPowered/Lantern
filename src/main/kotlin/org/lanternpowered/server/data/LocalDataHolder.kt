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
import org.lanternpowered.server.data.property.PropertyHolderBase
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

interface LocalDataHolder : ValueContainerBase, DataHolderBase, PropertyHolderBase {

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
    override fun supports(key: Key<*>) = supportsKey(key.uncheckedCast<Key<Value<Any>>>())

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

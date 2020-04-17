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
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.key.BoundedValueKey
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.DirectionRelativeDataProvider
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.Consumer

class LanternLocalKeyRegistry<H : DataHolder> : LocalKeyRegistry<H>() {

    private val map = ConcurrentHashMap<Key<*>, LocalKeyRegistration<*, *, H>>()

    override val registrations = this.map.values.asUnmodifiableCollection()
    override val keys = this.map.keys.asUnmodifiableCollection()

    override fun <V : Value<E>, E : Any> get(key: Key<V>): LocalKeyRegistration<V, E, H>? = this.map[key].uncheckedCast()

    override fun <V : Value<E>, E : Any> getAsElement(key: Key<V>): ElementKeyRegistration<V, E, H>? =
            (this.map[key] as? ElementKeyRegistration<*,*,*>).uncheckedCast()

    override fun <H : DataHolder> forHolder(holderType: Class<H>): LocalKeyRegistry<H> = uncheckedCast()

    private fun checkRegistration(key: Key<*>) {
        check(!this.map.containsKey(key)) { "The key ${key.key} is already registered." }
    }

    private fun <V : Value<E>, E : Any> registerElement(key: Key<V>): ElementKeyRegistration<V, E, H> {
        checkRegistration(key)
        val registration = LanternElementKeyRegistration<V, E, H>(key)
        this.map[key] = registration
        return registration
    }

    private fun <V : BoundedValue<E>, E : Any> registerBoundedElement(key: Key<V>): BoundedElementKeyRegistration<V, E, H> {
        checkRegistration(key)
        val registration = LanternBoundedElementKeyRegistration<V, E, H>(key)
        this.map[key] = registration
        return registration
    }

    override fun <V : Value<E>, E : Any> register(key: Key<V>): ElementKeyRegistration<V, E, H> {
        return if (key is BoundedValueKey<*,*>) {
            registerBoundedElement(key.uncheckedCast<Key<BoundedValue<Any>>>()).uncheckedCast()
        } else {
            registerElement(key)
        }.removable()
    }

    override fun <V : Value<E>, E : Any> register(key: Key<V>, initialElement: E): ElementKeyRegistration<V, E, H> {
        return if (key is BoundedValueKey<*,*>) {
            registerBoundedElement(key.uncheckedCast<Key<BoundedValue<Any>>>()).uncheckedCast()
        } else {
            registerElement(key)
        }.nonRemovable().set(initialElement)
    }

    override fun <V : BoundedValue<E>, E : Any> register(key: Key<V>, initialElement: E): BoundedElementKeyRegistration<V, E, H> {
        return registerBoundedElement(key).nonRemovable().set(initialElement)
    }

    override fun <V : BoundedValue<E>, E : Any> register(key: Key<V>): BoundedElementKeyRegistration<V, E, H> {
        return registerBoundedElement(key).removable()
    }

    override fun <V : Value<E>, E : Any> registerProvider(key: Key<V>, provider: DataProvider<V, E>): LocalKeyRegistration<V, E, H> {
        checkRegistration(key)
        val dataProvider = when (provider) {
            is IDataProvider -> provider
            is DirectionRelativeDataProvider -> WrappedDirectionalDataProvider(provider)
            else -> WrappedDataProvider(provider)
        }
        val registration = LanternLocalProviderKeyRegistration<V, E, H>(key, dataProvider)
        this.map[key] = registration
        return registration
    }

    private fun <V : Value<E>, E : Any> registerProvider0(
            key: Key<V>, fn: LanternLocalDataProviderBuilder<V, E, H>.(key: Key<V>) -> Unit): LocalKeyRegistration<V, E, H> {
        checkRegistration(key)
        val builder = LanternLocalDataProviderBuilder<V, E, H>(key)
        builder.fn(key)
        val provider = builder.build()
        val registration = LanternLocalProviderKeyRegistration<V, E, H>(key, provider)
        this.map[key] = registration
        return registration
    }

    override fun <V : Value<E>, E : Any> registerProvider(
            key: Key<V>, fn: LocalDataProviderBuilder<V, E, H>.(key: Key<V>) -> Unit): LocalKeyRegistration<V, E, H> {
        return registerProvider0(key, fn)
    }

    override fun <V : Value<E>, E : Any> registerProvider(
            key: Key<V>, fn: BiConsumer<LocalJDataProviderBuilder<V, E, H>, Key<V>>): LocalKeyRegistration<V, E, H> {
        return registerProvider0(key, fn::accept)
    }

    override fun <V : Value<E>, E : Any> registerProvider(
            key: Key<V>, fn: Consumer<LocalJDataProviderBuilder<V, E, H>>): LocalKeyRegistration<V, E, H> {
        return registerProvider0(key) { fn.accept(this) }
    }

    override fun copy(): LocalKeyRegistry<H> {
        val copy = LanternLocalKeyRegistry<H>()
        for ((key, registration) in this.map) {
            copy.map[key] = (registration as LanternLocalKeyRegistration<*, *, H>).copy()
        }
        return copy
    }

    override fun remove(key: Key<*>) {
        this.map.remove(key)
    }
}

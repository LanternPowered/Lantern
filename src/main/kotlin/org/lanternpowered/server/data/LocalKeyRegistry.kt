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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.server.data

import org.lanternpowered.api.ext.*
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value
import java.util.function.BiConsumer

/**
 * Represents a collection of [KeyRegistration]s with
 * optionally bound element holders.
 */
abstract class LocalKeyRegistry<H : DataHolder> : KeyRegistry<LocalKeyRegistration<*, *, H>> {

    companion object {

        /**
         * Creates a new [LocalKeyRegistry].
         */
        @JvmStatic
        fun <H : DataHolder> of(): LocalKeyRegistry<H> {
            return LanternLocalKeyRegistry()
        }
    }

    /**
     * Gets this [LocalKeyRegistry] as a collection which targets the given [DataHolder] type.
     *
     * @param holderType The data holder type
     * @return This value collection, for the given holder type
     */
    abstract fun <H : DataHolder> forHolder(holderType: Class<H>): LocalKeyRegistry<H>

    /**
     * Gets this [LocalKeyRegistry] as a collection which targets the given [DataHolder] type [H].
     *
     * @return This value collection, for the given holder type
     */
    inline fun <H : DataHolder> forHolderUnchecked() = uncheckedCast<LocalKeyRegistry<H>>()

    /**
     * Gets this [LocalKeyRegistry] as a collection which targets the given [DataHolder] type [H].
     *
     * @return This value collection, for the given holder type
     */
    inline fun <reified H : DataHolder> forHolder() = forHolder(H::class.java)

    /**
     * A convenient alternative for the [apply] function on this collection. Applied to the specified holder type.
     */
    inline fun <reified H : DataHolder> forHolder(fn: LocalKeyRegistry<H>.() -> Unit) = forHolder<H>().apply(fn)

    /**
     * Gets the [KeyRegistration] for the given [Key], if present.
     *
     * @param key The key to get the registration for
     * @return The key registration, if found
     */
    abstract override operator fun <V : Value<E>, E : Any> get(key: Key<V>): LocalKeyRegistration<V, E, H>?

    /**
     * Gets the [ElementKeyRegistration] for the given [Key], if present.
     *
     * @param key The key to get the registration for
     * @return The key registration, if found
     */
    abstract fun <V : Value<E>, E : Any> getAsElement(key: Key<V>): ElementKeyRegistration<V, E, H>?

    /**
     * Registers the given [Key] to this value collection.
     *
     * By default are registrations registered using this
     * method removable. This can be changed by explicitly
     * calling [ElementKeyRegistration.nonRemovable].
     *
     * @param key The key to register
     * @return The element key registration
     */
    abstract fun <V : Value<E>, E : Any> register(key: Key<V>): ElementKeyRegistration<V, E, H>

    /**
     * Registers the given [Key] to this value
     * collection with the initial element.
     *
     * By default are registrations registered using this
     * method non-removable. This can be changed by explicitly
     * calling [ElementKeyRegistration.removable].
     *
     * @param key The key to register
     * @param initialElement The initial element
     * @return The element key registration
     */
    abstract fun <V : Value<E>, E : Any> register(key: Key<V>, initialElement: E): ElementKeyRegistration<V, E, H>

    /**
     * Registers the given [Key] with bounded value to this value collection.
     *
     * @param key The key to register
     * @return The bounded element key registration
     */
    abstract fun <V : BoundedValue<E>, E : Any> register(key: Key<V>): BoundedElementKeyRegistration<V, E, H>

    /**
     * Registers the given [Key] with bounded value to this value collection.
     *
     * @param key The key to register
     * @param initialElement The initial element
     * @return The bounded element key registration
     */
    abstract fun <V : BoundedValue<E>, E : Any> register(key: Key<V>, initialElement: E): BoundedElementKeyRegistration<V, E, H>

    /**
     * Registers the given [Key] with the data provider to this value collection.
     *
     * @param key The key to register
     * @return The key registration
     */
    abstract fun <V : Value<E>, E : Any> registerProvider(key: Key<V>, provider: DataProvider<V, E>): LocalKeyRegistration<V, E, H>

    /**
     * Registers the given [Key] with the local data provider to this value collection.
     *
     * @param key The key to register
     * @return The key registration
     */
    abstract fun <V : Value<E>, E : Any> registerProvider(
            key: Key<V>, fn: LocalDataProviderBuilder<V, E, H>.(key: Key<V>) -> Unit): LocalKeyRegistration<V, E, H>

    /**
     * Registers the given [Key] with the local data provider to this value collection.
     *
     * @param key The key to register
     * @return The key registration
     */
    abstract fun <V : Value<E>, E : Any> registerProvider(
            key: Key<V>, fn: BiConsumer<LocalJDataProviderBuilder<V, E, H>, Key<V>>): LocalKeyRegistration<V, E, H>

    /**
     * Removes the registration for the given [Key].
     */
    abstract fun remove(key: Key<*>)

    /**
     * Creates a copy of this [LocalKeyRegistry].
     *
     * @return The copy
     */
    abstract fun copy(): LocalKeyRegistry<H>
}

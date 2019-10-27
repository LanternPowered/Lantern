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

package org.lanternpowered.server.data.property

import org.lanternpowered.api.data.property.DoublePropertyProvider
import org.lanternpowered.api.data.property.IntPropertyProvider
import org.lanternpowered.api.data.property.Property
import org.lanternpowered.api.data.property.PropertyHolder
import org.lanternpowered.api.data.property.PropertyProvider
import org.lanternpowered.api.ext.uncheckedCast

@PropertyDsl
abstract class PropertyRegistry<H : PropertyHolder> {

    companion object {

        @JvmStatic
        fun <H : PropertyHolder> of(): PropertyRegistry<H> {
            return LanternPropertyRegistry()
        }
    }

    /**
     * A map with all the (combined) property providers.
     */
    abstract val providers: Map<Property<*>, PropertyProvider<*>>

    /**
     * Gets this [PropertyRegistry] as a registry which targets the given [PropertyHolder] type.
     *
     * @param holderType The data holder type
     * @return This local property registry, for the given holder type
     */
    abstract fun <H : PropertyHolder> forHolder(holderType: Class<H>): PropertyRegistry<H>

    /**
     * Gets this [PropertyRegistry] as a registry which targets the given [PropertyHolder] type [H].
     *
     * @return This local property, for the given holder type
     */
    inline fun <H : PropertyHolder> forHolderUnchecked() = uncheckedCast<PropertyRegistry<H>>()

    /**
     * Gets this [PropertyRegistry] as a registry which targets the given [PropertyHolder] type [H].
     *
     * @return This local property registry, for the given holder type
     */
    inline fun <reified H : PropertyHolder> forHolder() = forHolder(H::class.java)

    /**
     * A convenient alternative for the [apply] function on this collection. Applied to the specified holder type.
     */
    inline fun <reified H : PropertyHolder> forHolder(fn: PropertyRegistry<H>.() -> Unit) = forHolder<H>().apply(fn)

    /**
     * Registers the provided [PropertyProvider] for the given
     * [Property]. Note that only a single [PropertyProvider]
     * can be registered per [Property]. Multiple
     * registrations will result in exceptions being thrown.
     *
     * @param property The property to register the provider for
     * @param constant The constant value
     * @param V The value type of the property
     */
    abstract fun <V : Any> register(property: Property<V>, constant: V)

    /**
     * Registers the provided [PropertyProvider] for the given
     * [Property]. Note that only a single [PropertyProvider]
     * can be registered per [Property]. Multiple
     * registrations will result in exceptions being thrown.
     *
     * @param property The property to register the provider for
     * @param propertyProvider The property provider
     * @param V The value type of the property
     */
    abstract fun <V : Any> registerProvider(
            property: Property<V>, propertyProvider: PropertyProvider<V>)

    /**
     * Registers a [PropertyProvider] that is built with the given function.
     *
     * @param property The property
     * @param fn The builder function
     */
    abstract fun <V : Any> registerProvider(
            property: Property<V>, fn: PropertyProviderBuilder<V, H>.(property: Property<V>) -> Unit)

    /**
     * Retrieves the [PropertyProvider] associated for the provided
     * [Property].
     *
     * If there are no registered [PropertyProvider]s, then will the
     * returned provider always return empty.
     *
     * @param property The property
     * @param V The value type of the property
     * @return The property provider
     */
   abstract fun <V : Any> getProvider(property: Property<V>): PropertyProvider<V>

    /**
     * Retrieves the [IntPropertyProvider] associated for the provided
     * [Property].
     *
     * If there are no registered [PropertyProvider]s, then will the
     * returned provider always return empty.
     *
     * @param property The property
     * @return The property provider
     */
    abstract fun getIntProvider(property: Property<Int>): IntPropertyProvider

    /**
     * Retrieves the [DoublePropertyProvider] associated for the provided
     * [Property].
     *
     * If there are no registered [PropertyProvider]s, then will the
     * returned provider always return empty.
     *
     * @param property The property
     * @return The property provider
     */
    abstract fun getDoubleProvider(property: Property<Double>): DoublePropertyProvider
}

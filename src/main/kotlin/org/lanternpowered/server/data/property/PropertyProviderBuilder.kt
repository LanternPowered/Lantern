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
@file:Suppress("unused")

package org.lanternpowered.server.data.property

import org.lanternpowered.api.ext.optional
import org.lanternpowered.api.ext.typeTokenOf
import org.lanternpowered.api.util.Direction
import org.lanternpowered.api.util.TypeToken
import org.spongepowered.api.data.property.DirectionRelativePropertyHolder
import org.spongepowered.api.data.property.PropertyHolder
import java.util.*

@PropertyDsl
abstract class PropertyProviderBuilder<V : Any, H : PropertyHolder> {

    /**
     * Sets the priority of the property store.
     *
     * The priority is only used when targeting a specific
     * [PropertyRegistry]. E.g. local property providers will
     * always have priority over global ones.
     *
     * @param priority The priority
     * @return This builder, for chaining
     */
    abstract fun priority(priority: Int): PropertyProviderBuilder<V, H>

    /**
     * Gets this builder as a builder to target the given
     * [PropertyHolder] type and applies the builder function.
     *
     * This can be done multiple times for multiple holder types.
     *
     * @param holderType The holder type
     * @param fn The builder function to apply
     * @return This builder targeting the specified holder type, for chaining
     */
    fun <N : H> forHolder(holderType: TypeToken<N>, fn: PropertyProviderBuilder<V, N>.() -> Unit)
            = forHolder(holderType).apply(fn)

    /**
     * Gets this builder as a builder to target the given
     * [PropertyHolder] type and applies the builder function.
     *
     * This can be done multiple times for multiple holder types.
     *
     * @param N The holder type
     * @param fn The builder function to apply
     * @return This builder targeting the specified holder type, for chaining
     */
    @JvmSynthetic
    inline fun <reified N : H> forHolder(fn: PropertyProviderBuilder<V, N>.() -> Unit = {})
            = forHolder(typeTokenOf<N>()).apply(fn)

    /**
     * Gets this builder as a builder to target the given
     * [PropertyHolder] type.
     *
     * This can be done multiple times for multiple holder types.
     *
     * @param holderType The holder type
     * @return This builder targeting the specified holder type, for chaining
     */
    abstract fun <N : H> forHolder(holderType: TypeToken<N>): PropertyProviderBuilder<V, N>

    /**
     * Sets the get function for the current holder type [H].
     *
     * @param fn The function to set
     * @return This builder, for chaining
     */
    inline fun get(crossinline fn: @PropertyDsl H.() -> V?) = getOptional { fn().optional() }

    /**
     * Sets the get function for the current holder type [H].
     *
     * @param fn The function to set
     * @return This builder, for chaining
     */
    abstract fun getOptional(fn: @PropertyDsl H.() -> Optional<V>): PropertyProviderBuilder<V, H>

    /**
     * Sets the direction based get function for the current holder type [H].
     *
     * @param fn The function to set
     * @return This builder, for chaining
     */
    inline fun <H> PropertyProviderBuilder<V, H>.get(crossinline fn: @PropertyDsl H.(direction: Direction) -> V?):
            PropertyProviderBuilder<V, H> where H : PropertyHolder, H : DirectionRelativePropertyHolder {
        return getOptional { direction -> fn(direction).optional() }
    }

    /**
     * Sets the direction based get function for the current holder type [H].
     *
     * @param fn The function to set
     * @return This builder, for chaining
     */
    @JvmSynthetic
    abstract fun <H> PropertyProviderBuilder<V, H>.getOptional(
            fn: @PropertyDsl H.(direction: Direction) -> Optional<V>
    ): PropertyProviderBuilder<V, H> where H : PropertyHolder, H : DirectionRelativePropertyHolder
}

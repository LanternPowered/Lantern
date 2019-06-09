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

import org.lanternpowered.server.util.function.TriConsumer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value

/**
 * Represents the [ElementKeyRegistration] of a bounded value.
 */
interface BoundedElementKeyRegistration<V : BoundedValue<E>, E : Any, H : DataHolder> : ElementKeyRegistration<V, E, H> {

    operator fun invoke(fn: BoundedElementKeyRegistration<V, E, H>.() -> Unit) = apply(fn)

    /**
     * Sets the minimum value of this key registration.
     *
     * @param minimum The minimum value
     * @return This registration, for chaining
     */
    fun minimum(minimum: E): BoundedElementKeyRegistration<V, E, H>

    /**
     * Sets the supplier of the minimum value of this key registration.
     *
     * @param minimum The minimum value supplier
     * @return This registration, for chaining
     */
    fun minimum(minimum: H.() -> E): BoundedElementKeyRegistration<V, E, H>

    /**
     * Sets the minimum value key of this key registration.
     *
     * @param minimum The minimum value key
     * @return This registration, for chaining
     */
    fun minimum(minimum: Key<out Value<E>>): BoundedElementKeyRegistration<V, E, H>

    /**
     * Sets the maximum value of this key registration.
     *
     * @param maximum The maximum value
     * @return This registration, for chaining
     */
    fun maximum(maximum: E): BoundedElementKeyRegistration<V, E, H>

    /**
     * Sets the supplier of the maximum value of this key registration.
     *
     * @param maximum The maximum value supplier
     * @return This registration, for chaining
     */
    fun maximum(maximum: H.() -> E): BoundedElementKeyRegistration<V, E, H>

    /**
     * Sets the maximum value key of this key registration.
     *
     * @param maximum The maximum value key
     * @return This registration, for chaining
     */
    fun maximum(maximum: Key<out Value<E>>): BoundedElementKeyRegistration<V, E, H>

    /**
     * Applies a validator which checks if the element [E] is valid.
     *
     * Setting the validator won't affect the bound checks
     * that will be performed.
     *
     * @param validator The validator to set
     * @return This registration, for chaining
     */
    override fun validator(validator: H.(element: E) -> Boolean): BoundedElementKeyRegistration<V, E, H>

    override fun set(element: E): BoundedElementKeyRegistration<V, E, H>

    override fun remove(): BoundedElementKeyRegistration<V, E, H>

    override fun nonRemovable(): BoundedElementKeyRegistration<V, E, H>

    override fun removable(): BoundedElementKeyRegistration<V, E, H>

    override fun addChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit): BoundedElementKeyRegistration<V, E, H>

    override fun addChangeListener(listener: H.(newValue: E?) -> Unit): BoundedElementKeyRegistration<V, E, H>

    override fun addChangeListener(listener: H.() -> Unit): BoundedElementKeyRegistration<V, E, H>

    override fun addChangeListener(listener: TriConsumer<H, E?, E?>): BoundedElementKeyRegistration<V, E, H>
}

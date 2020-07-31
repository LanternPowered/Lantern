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

import org.lanternpowered.server.util.function.TriConsumer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.function.Supplier

/**
 * Represents the [ElementKeyRegistration] of a bounded value.
 */
interface BoundedElementKeyRegistration<V : Value<E>, E : Any, H : DataHolder> : ElementKeyRegistration<V, E, H> {

    operator fun invoke(fn: BoundedElementKeyRegistration<V, E, H>.() -> Unit) = apply(fn)

    /**
     * When enabled, coerces offered values automatically within the bounds of the
     * registration. Instead of failing when the value is outside the bounds.
     *
     * @return This registration, for chaining
     */
    fun coerceInBounds(): BoundedElementKeyRegistration<V, E, H>

    /**
     * Sets the value range of this key registration.
     *
     * @param range The value range
     * @return This registration, for chaining
     */
    fun <V : Value<E>, E : Comparable<E>, H : DataHolder> BoundedElementKeyRegistration<V, E, H>
            .range(range: ClosedRange<E>): BoundedElementKeyRegistration<V, E, H>

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
     * Sets the minimum value key of this key registration.
     *
     * @param minimum The minimum value key
     * @return This registration, for chaining
     */
    fun minimum(minimum: Supplier<out Key<out Value<E>>>): BoundedElementKeyRegistration<V, E, H> = minimum(minimum.get())

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
     * Sets the maximum value key of this key registration.
     *
     * @param maximum The maximum value key
     * @return This registration, for chaining
     */
    fun maximum(maximum: Supplier<out Key<out Value<E>>>): BoundedElementKeyRegistration<V, E, H> = maximum(maximum.get())

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

    @JvmSynthetic
    override fun addChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit): BoundedElementKeyRegistration<V, E, H>

    @JvmSynthetic
    override fun addChangeListener(listener: H.(newValue: E?) -> Unit): BoundedElementKeyRegistration<V, E, H>

    @JvmSynthetic
    override fun addChangeListener(listener: H.() -> Unit): BoundedElementKeyRegistration<V, E, H>

    override fun addChangeListener(listener: TriConsumer<H, E?, E?>): BoundedElementKeyRegistration<V, E, H>
}

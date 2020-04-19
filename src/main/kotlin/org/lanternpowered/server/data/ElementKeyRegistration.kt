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
import org.spongepowered.api.data.value.Value

/**
 * Represents a [KeyRegistration] that is tied to
 * a specific [LocalKeyRegistry].
 */
interface ElementKeyRegistration<V : Value<E>, E : Any, H : DataHolder> : LocalKeyRegistration<V, E, H> {

    operator fun invoke(fn: ElementKeyRegistration<V, E, H>.() -> Unit) = apply(fn)

    /**
     * Gets the backing element, if it exists.
     *
     * @return The backing element
     */
    fun get(): E?

    /**
     * Sets the backing element.
     *
     * Setting the element through this method bypasses all the
     * change listeners registered with [addChangeListener].
     *
     * @return This registration, for chaining
     */
    fun set(element: E): ElementKeyRegistration<V, E, H>

    /**
     * Sets the backing element as "removed", if this is supported.
     *
     * Removing the element through this method bypasses all the
     * change listeners registered with [addChangeListener].
     *
     * @return This registration, for chaining
     */
    fun remove(): ElementKeyRegistration<V, E, H>

    /**
     * Applies a validator which checks if the element [E] is valid.
     *
     * @param validator The validator to set
     * @return This registration, for chaining
     */
    fun validator(validator: H.(element: E) -> Boolean): ElementKeyRegistration<V, E, H>

    /**
     * Marks this registration as non-removable.
     *
     * @return This registration, for chaining
     */
    fun nonRemovable(): ElementKeyRegistration<V, E, H>

    /**
     * Marks this registration as removable.
     *
     * @return This registration, for chaining
     */
    fun removable(): ElementKeyRegistration<V, E, H>

    @JvmSynthetic
    override fun addChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit): ElementKeyRegistration<V, E, H>

    @JvmSynthetic
    override fun addChangeListener(listener: H.(newValue: E?) -> Unit): ElementKeyRegistration<V, E, H>

    @JvmSynthetic
    override fun addChangeListener(listener: H.() -> Unit): ElementKeyRegistration<V, E, H>

    override fun addChangeListener(listener: TriConsumer<H, E?, E?>): ElementKeyRegistration<V, E, H>
}

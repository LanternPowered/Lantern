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

@LocalDataDsl
interface LocalKeyRegistration<V : Value<E>, E : Any, H : DataHolder> : KeyRegistration<V, E> {

    operator fun invoke(fn: LocalKeyRegistration<V, E, H>.() -> Unit) = apply(fn)

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    @JvmSynthetic
    fun addChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit): LocalKeyRegistration<V, E, H>

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    @JvmSynthetic
    fun addChangeListener(listener: H.(newValue: E?) -> Unit): LocalKeyRegistration<V, E, H>

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    @JvmSynthetic
    fun addChangeListener(listener: H.() -> Unit): LocalKeyRegistration<V, E, H>

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    fun addChangeListener(listener: TriConsumer<H, E?, E?>): LocalKeyRegistration<V, E, H>

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    @JvmSynthetic
    fun removeChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit): LocalKeyRegistration<V, E, H>

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    @JvmSynthetic
    fun removeChangeListener(listener: H.(newValue: E?) -> Unit): LocalKeyRegistration<V, E, H>

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    @JvmSynthetic
    fun removeChangeListener(listener: H.() -> Unit): LocalKeyRegistration<V, E, H>

    /**
     * Adds a listener that will be notified when the value changes.
     *
     * @param listener The listener to add
     * @return This registration, for chaining
     */
    fun removeChangeListener(listener: TriConsumer<H, E?, E?>): LocalKeyRegistration<V, E, H>
}

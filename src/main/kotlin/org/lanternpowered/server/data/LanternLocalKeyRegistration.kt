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

internal abstract class LanternLocalKeyRegistration<V : Value<E>, E : Any, H : DataHolder>(key: Key<V>) :
        LanternKeyRegistration<V, E>(key), LocalKeyRegistration<V, E, H> {

    /**
     * All the change listeners that are registered.
     */
    protected var changeListeners: MutableList<H.(newValue: E?, oldValue: E?) -> Unit>? = null

    override fun addChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit) = apply {
        var changeListeners = this.changeListeners
        if (changeListeners == null) {
            changeListeners = mutableListOf<H.(E?, E?) -> Unit>().also { this.changeListeners = it }
        }
        changeListeners.add(listener)
    }

    override fun addChangeListener(listener: H.(newValue: E?) -> Unit) = addChangeListener { newValue, _ -> listener(newValue) }

    override fun addChangeListener(listener: H.() -> Unit) = addChangeListener { _, _ -> listener() }

    override fun addChangeListener(listener: TriConsumer<H, E?, E?>) = addChangeListener(listener::apply)

    /**
     * Creates a copy of this local key registration.
     *
     * @return The created copy
     */
    abstract fun copy(): LanternLocalKeyRegistration<V, E, H>
}

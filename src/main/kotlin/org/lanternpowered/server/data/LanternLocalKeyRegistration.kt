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

typealias ChangeListener<H, E> = H.(newValue: E?, oldValue: E?) -> Unit

internal abstract class LanternLocalKeyRegistration<V : Value<E>, E : Any, H : DataHolder>(key: Key<V>) :
        LanternKeyRegistration<V, E>(key), LocalKeyRegistration<V, E, H> {

    /**
     * All the change listeners that are registered.
     */
    protected var changeListeners: MutableList<ChangeListener<H, E>>? = null

    private interface ChangeListenerImpl<H, E> : (H, E?, E?) -> Unit {
        val instance: Any
    }

    private fun ChangeListener<*, *>.instance(): Any =
            if (this is ChangeListenerImpl) this.instance else this

    private class ChangeListenerNoOldValue<H, E>(
            override val instance: H.(newValue: E?) -> Unit
    ) : ChangeListenerImpl<H, E> {
        override fun invoke(holder: H, newValue: E?, oldValue: E?) {
            this.instance(holder, newValue)
        }
    }

    private class ChangeListenerNoValue<H, E>(
            override val instance: H.() -> Unit
    ) : ChangeListenerImpl<H, E> {
        override fun invoke(holder: H, newValue: E?, oldValue: E?) {
            this.instance(holder)
        }
    }

    private class ChangeListenerTriConsumer<H, E>(
            override val instance: TriConsumer<H, E?, E?>
    ) : ChangeListenerImpl<H, E> {
        override fun invoke(holder: H, newValue: E?, oldValue: E?) {
            this.instance.apply(holder, newValue, oldValue)
        }
    }

    override fun addChangeListener(listener: ChangeListener<H, E>) = apply {
        var changeListeners = this.changeListeners
        if (changeListeners == null) {
            changeListeners = mutableListOf<ChangeListener<H, E>>().also { this.changeListeners = it }
        }
        changeListeners.add(listener)
    }

    override fun addChangeListener(listener: H.(newValue: E?) -> Unit) = this.addChangeListener(ChangeListenerNoOldValue(listener))

    override fun addChangeListener(listener: H.() -> Unit) = this.addChangeListener(ChangeListenerNoValue(listener))

    override fun addChangeListener(listener: TriConsumer<H, E?, E?>) = this.addChangeListener(ChangeListenerTriConsumer(listener))

    override fun removeChangeListener(listener: H.() -> Unit): LocalKeyRegistration<V, E, H> =
            this.removeChangeListener(listener as Any)

    override fun removeChangeListener(listener: H.(newValue: E?) -> Unit): LocalKeyRegistration<V, E, H> =
            this.removeChangeListener(listener as Any)

    override fun removeChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit): LocalKeyRegistration<V, E, H> =
            this.removeChangeListener(listener as Any)

    override fun removeChangeListener(listener: TriConsumer<H, E?, E?>): LocalKeyRegistration<V, E, H> =
            this.removeChangeListener(listener as Any)

    private fun removeChangeListener(listener: Any): LocalKeyRegistration<V, E, H> {
        val changeListeners = this.changeListeners ?: return this
        changeListeners.removeIf { other -> other.instance() === listener }
        return this
    }

    /**
     * Creates a copy of this local key registration.
     *
     * @return The created copy
     */
    abstract fun copy(): LanternLocalKeyRegistration<V, E, H>
}

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
    protected abstract fun copy(): LanternLocalKeyRegistration<V, E, H>
}

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

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.key.BoundedValueKey
import org.lanternpowered.server.data.value.CopyHelper
import org.lanternpowered.server.util.function.TriConsumer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value

@Suppress("UNCHECKED_CAST")
internal class LanternBoundedElementKeyRegistration<V : BoundedValue<E>, E : Any, H : DataHolder>(key: Key<V>) :
        LanternElementKeyRegistration<V, E, H>(key), BoundedElementKeyRegistration<V, E, H> {

    private var minimum: (H.() -> E?)? = null
    private var maximum: (H.() -> E?)? = null

    override fun <V : BoundedValue<E>, E : Comparable<E>, H : DataHolder> BoundedElementKeyRegistration<V, E, H>
            .range(range: ClosedRange<E>) = apply {
        this as LanternBoundedElementKeyRegistration<V, E, H>
        minimum(range.start)
        maximum(range.endInclusive)
    }

    override fun minimum(minimum: E) = apply {
        // If a copy will exactly be the same, eliminate
        // the redundant copy call
        if (CopyHelper.copy(minimum) === minimum) {
            minimum { minimum }
        } else {
            minimum { CopyHelper.copy(minimum) }
        }
    }

    override fun minimum(minimum: H.() -> E) = apply {
        this.minimum = minimum
    }

    override fun minimum(minimum: Key<out Value<E>>) = apply {
        this.minimum = { get(minimum).orNull() }
    }

    override fun maximum(maximum: E)  = apply {
        // If a copy will exactly be the same, eliminate
        // the redundant copy call
        if (CopyHelper.copy(maximum) === maximum) {
            maximum { maximum }
        } else {
            maximum { CopyHelper.copy(maximum) }
        }
    }

    override fun maximum(maximum: H.() -> E) = apply {
        this.maximum = maximum
    }

    override fun maximum(maximum: Key<out Value<E>>) = apply {
        this.maximum = { get(maximum).orNull() }
    }

    override fun validate(holder: H, element: E): Boolean {
        val key = this.key as BoundedValueKey<V, E>
        val comparator = key.comparator

        val minimum = this.minimum?.invoke(holder) ?: key.minimum()
        val maximum = this.maximum?.invoke(holder) ?: key.maximum()
        if (comparator.compare(element, minimum) < 0 || comparator.compare(element, maximum) > 0) {
            return false
        }

        return super.validate(holder, element)
    }

    override fun immutableValueOf(holder: H, element: E): Value.Immutable<E> {
        val key = this.key as BoundedValueKey<V, E>

        val minimum = this.minimum?.invoke(holder) ?: key.minimum()
        val maximum = this.maximum?.invoke(holder) ?: key.maximum()

        return BoundedValue.immutableOf(key, element, minimum, maximum).asImmutable()
    }

    override fun validator(validator: H.(element: E) -> Boolean) = apply { super.validator(validator) }
    override fun set(element: E) = apply { super.set(element) }
    override fun nonRemovable() = apply { super.nonRemovable() }
    override fun removable() = apply { super.removable() }
    override fun remove() = apply { super.remove() }

    override fun addChangeListener(listener: H.(newValue: E?, oldValue: E?) -> Unit) = apply { super.addChangeListener(listener) }
    override fun addChangeListener(listener: H.(newValue: E?) -> Unit) = apply { super.addChangeListener(listener) }
    override fun addChangeListener(listener: H.() -> Unit) = apply { super.addChangeListener(listener) }
    override fun addChangeListener(listener: TriConsumer<H, E?, E?>) = apply { super.addChangeListener(listener) }

    override fun copy(): LanternLocalKeyRegistration<V, E, H> {
        val copy = LanternBoundedElementKeyRegistration<V, E, H>(this.key)
        copyTo(copy)
        copy.minimum = this.minimum
        copy.maximum = this.maximum
        return copy
    }
}

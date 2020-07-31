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

import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.value.immutableValueOf
import org.lanternpowered.server.data.key.ValueKey
import org.lanternpowered.server.data.value.CopyHelper
import org.lanternpowered.server.util.function.TriConsumer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

@Suppress("UNCHECKED_CAST")
internal class LanternBoundedElementKeyRegistration<V : Value<E>, E : Any, H : DataHolder>(key: Key<V>) :
        LanternElementKeyRegistration<V, E, H>(key), BoundedElementKeyRegistration<V, E, H> {

    private var minimum: (H.() -> E?)? = null
    private var maximum: (H.() -> E?)? = null
    private var coerceInBounds = false

    override fun coerceInBounds(): BoundedElementKeyRegistration<V, E, H> = apply { this.coerceInBounds = true }

    override fun <V : Value<E>, E : Comparable<E>, H : DataHolder> BoundedElementKeyRegistration<V, E, H>
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

    override fun transform(holder: H, element: E): E {
        if (!this.coerceInBounds)
            return super.transform(holder, element)

        val key = this.key as ValueKey<V, E>
        val comparator = key.elementComparator

        val minimum = this.minimum?.invoke(holder)
        if (minimum != null && comparator.compare(element, minimum) < 0)
            return minimum

        val maximum = this.maximum?.invoke(holder)
        if (maximum != null && comparator.compare(element, maximum) > 0)
            return maximum

        return super.transform(holder, element)
    }

    override fun validate(holder: H, element: E): Boolean {
        if (this.coerceInBounds)
            return super.validate(holder, element)

        val key = this.key as ValueKey<V, E>
        val comparator = key.elementComparator

        val minimum = this.minimum?.invoke(holder)
        if (minimum != null && comparator.compare(element, minimum) < 0)
            return false

        val maximum = this.maximum?.invoke(holder)
        if (maximum != null && comparator.compare(element, maximum) > 0)
            return false

        return super.validate(holder, element)
    }

    override fun immutableValueOf(holder: H, element: E): Value.Immutable<E> = immutableValueOf(this.key, element)

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

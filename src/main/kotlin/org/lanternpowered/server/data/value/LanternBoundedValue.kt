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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.key.BoundedValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import java.util.*

abstract class LanternBoundedValue<E : Any> protected constructor(
        key: Key<out BoundedValue<E>>, value: E, protected val min: () -> E, protected val max: () -> E
) : LanternValue<E>(key, value), BoundedValue<E> {

    override fun getKey() = super.getKey().uncheckedCast<BoundedValueKey<out BoundedValue<E>, E>>()

    override fun getMinValue() = this.min()
    override fun getMaxValue() = this.max()
    override fun getComparator() = this.key.elementComparator

    override fun hashCode() = Objects.hash(this.key, this.value, this.min, this.max, this.comparator)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || this.javaClass != other.javaClass) {
            return false
        }
        other as LanternBoundedValue<*>?
        return this.key == other.key
                && this.value == other.value
                && this.min == other.min
                && this.max == other.max
                && this.comparator == other.comparator
    }

    override fun toString() = ToStringHelper(this)
            .add("key", this.key)
            .add("value", this.value)
            .add("min", this.min)
            .add("max", this.max)
            .add("comparator", this.comparator)
            .toString()
}

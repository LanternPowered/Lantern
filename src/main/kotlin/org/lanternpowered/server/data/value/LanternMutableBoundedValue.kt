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

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import java.util.function.Function

class LanternMutableBoundedValue<E : Any>(
        key: Key<out BoundedValue<E>>, value: E, min: () -> E, max: () -> E
) : LanternBoundedValue<E>(key, value, min, max), BoundedValue.Mutable<E> {

    override fun set(value: E) = LanternMutableBoundedValue(this.key, value, this.min, this.max)

    override fun transform(function: Function<E, E>) = set(function.apply(get()))

    override fun asImmutable() =  LanternImmutableBoundedValue(this.key, CopyHelper.copy(this.value), this.min, this.max)

    override fun copy() = LanternMutableBoundedValue(this.key, CopyHelper.copy(this.value), this.min, this.max)
}

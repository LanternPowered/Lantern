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

class LanternImmutableBoundedValue<E : Any>(
        key: Key<out BoundedValue<E>>, value: E, min: () -> E, max: () -> E
) : LanternBoundedValue<E>(key, value, min, max), BoundedValue.Immutable<E> {

    override fun get(): E = CopyHelper.copy(super.get())

    override fun with(value: E): BoundedValue.Immutable<E> = this.key.valueConstructor.getImmutable(value, this.min, this.max).asImmutable()

    override fun transform(function: Function<E, E>) = with(function.apply(get()))

    override fun asMutable() = LanternMutableBoundedValue(this.key, CopyHelper.copy(value), this.min, this.max)
}

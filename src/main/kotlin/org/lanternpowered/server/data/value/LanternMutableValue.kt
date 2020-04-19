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
import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.function.Function

class LanternMutableValue<E : Any>(key: Key<out Value<E>>, value: E) : LanternValue<E>(key, value), Value.Mutable<E> {

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<Value<E>, E>>()

    override fun set(value: E) = apply { this.value = value }

    override fun transform(function: Function<E, E>) = set(function.apply(get()))

    override fun asImmutable(): Value.Immutable<E> = this.key.valueConstructor.getImmutable(this.value).asImmutable()

    override fun copy() = LanternMutableValue(this.key, CopyHelper.copy(this.value))
}

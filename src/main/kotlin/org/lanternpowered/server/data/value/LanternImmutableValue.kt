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

class LanternImmutableValue<E : Any>(key: Key<out Value<E>>, value: E) : LanternValue<E>(key, value), Value.Immutable<E> {

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<Value<E>, E>>()

    override fun get() = CopyHelper.copy(super.get())

    override fun with(value: E): Value.Immutable<E> = this.key.valueConstructor.getImmutable(value).asImmutable()

    override fun transform(function: Function<E, E>) = with(function.apply(get()))

    override fun asMutable() = LanternMutableValue(this.key, CopyHelper.copy(this.value))
}

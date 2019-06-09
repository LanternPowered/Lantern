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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.key.OptionalValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.OptionalValue
import org.spongepowered.api.data.value.Value
import java.util.Optional
import java.util.function.Function

class LanternMutableOptionalValue<E : Any>(
        key: Key<out OptionalValue<E>>, value: Optional<E>
) : LanternValue<Optional<E>>(key, value), OptionalValue.Mutable<E> {

    override fun getKey() = super.getKey().uncheckedCast<Key<out OptionalValue<E>>>()

    override fun orElse(defaultValue: E): Value.Mutable<E> {
        val unwrappedKey = (this.key as OptionalValueKey<*,*>).unwrappedKey.uncheckedCast<Key<out Value<E>>>()
        return LanternMutableValue(unwrappedKey, this.value.orElse(defaultValue))
    }

    override fun transform(function: Function<Optional<E>, Optional<E>>) = set(function.apply(get()))

    override fun asImmutable() = LanternImmutableOptionalValue(this.key, CopyHelper.copy(this.value))

    override fun set(value: Optional<E>) = LanternMutableOptionalValue(this.key, CopyHelper.copy(value))

    override fun copy() = LanternMutableOptionalValue(this.key, CopyHelper.copy(this.value))
}

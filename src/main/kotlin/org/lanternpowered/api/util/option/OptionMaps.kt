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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.api.util.option

import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet

/**
 * Base class for all the [OptionMap]s.
 */
abstract class OptionMapBase<T : OptionMapType> internal constructor(val map: MutableMap<Option<T, Any?>, Any?>) : OptionMap<T> {

    override fun options(): Collection<Option<T, *>> = this.map.keys.toImmutableSet()

    override operator fun <V> get(option: Option<T, V>): V {
        return (this.map[option as Option<T, Any?>] ?: option.defaultValue) as V
    }

    override operator fun contains(option: Option<T, *>): Boolean = (option as Option<T, Any?>) in this.map
}

/**
 * A unmodifiable [OptionMap].
 */
internal class UnmodifiableOptionMap<T : OptionMapType>(map: MutableMap<Option<T, Any?>, Any?>) : OptionMapBase<T>(map)

/**
 * A [HashMap] backed implementation of the [OptionMap].
 *
 * @param T The option map type
 */
class OptionHashMap<T : OptionMapType> : OptionMapBase<T>(HashMap()), MutableOptionMap<T> {

    override fun asUnmodifiable(): OptionMap<T> = UnmodifiableOptionMap(this.map)
    override fun toImmutable(): OptionMap<T> = UnmodifiableOptionMap(this.map.toImmutableMap())

    override operator fun <V> set(option: Option<T, V>, value: V) {
        this.map[option as Option<T, Any?>] = value as Any?
    }
}

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

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
package org.lanternpowered.api.util.option

/**
 * Represents a mutable version of the [OptionMap].
 */
interface MutableOptionMap<T : OptionMapType> : OptionMap<T> {

    /**
     * Sets the value for the given [Option].
     */
    operator fun <V> set(option: Option<T, V>, value: V)

    /**
     * Gets a unmodifiable view of this [OptionMap].
     */
    fun asUnmodifiable(): OptionMap<T>

    /**
     * Gets a immutable copy of this [OptionMap].
     */
    fun toImmutable(): OptionMap<T>
}

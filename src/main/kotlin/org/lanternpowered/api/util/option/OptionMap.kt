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
 * Represents a basic option map.
 *
 * @param T The option map type
 */
interface OptionMap<T : OptionMapType> {

    /**
     * Gets the value or default value for the given [Option].
     */
    operator fun <V> get(option: Option<T, V>): V

    /**
     * Gets whether the given [Option] is set in this [OptionMap].
     */
    operator fun contains(option: Option<T, *>): Boolean

    /**
     * Gets a [Collection] with all the applied options in this map.
     */
    fun options(): Collection<Option<T, *>>
}

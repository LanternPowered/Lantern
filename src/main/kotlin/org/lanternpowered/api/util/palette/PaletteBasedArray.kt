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
package org.lanternpowered.api.util.palette

import org.lanternpowered.api.util.VariableValueArray

/**
 * Represents an object array that stores objects
 * using a [VariableValueArray] and a [Palette].
 */
interface PaletteBasedArray<T : Any> {

    /**
     * The size.
     */
    val size: Int

    /**
     * Gets the [T] at the given index.
     *
     * @param index The index
     * @return The object
     */
    operator fun get(index: Int): T

    /**
     * Sets the [T] at the given index.
     *
     * @param index The index
     * @param obj The object
     * @return The previous assigned value
     */
    operator fun set(index: Int, obj: T)

    /**
     * Gets the [Palette] of this palette based object
     * array. Does not allow removal of [T]s.
     *
     * @return The object palette
     */
    val palette: Palette<T>

    /**
     * The backing [VariableValueArray] which holds the integer ids.
     */
    val backing: VariableValueArray

    /**
     * Creates a copy of this [PaletteBasedArray].
     *
     * @return The copy
     */
    fun copy(): PaletteBasedArray<T>

    /**
     * Serializes this [PaletteBasedArray].
     */
    fun serialize(): SerializedPaletteBasedArray<T>
}

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

interface PaletteBasedArrayFactory {

    fun <T : Any> of(size: Int, default: () -> T): PaletteBasedArray<T>

    fun <T : Any> of(size: Int, globalPalette: GlobalPalette<T>): PaletteBasedArray<T>

    fun <T : Any> of(values: IntArray, globalPalette: GlobalPalette<T>): PaletteBasedArray<T> =
            of(values, globalPalette) { globalPalette.default }

    fun <T : Any> of(values: IntArray, palette: Palette<T>, default: () -> T): PaletteBasedArray<T>

    fun <T : Any> of(values: IntArray, palette: Palette<T>, globalPalette: GlobalPalette<T>): PaletteBasedArray<T> =
            of(values, palette, globalPalette) { globalPalette.default }

    fun <T : Any> of(values: IntArray, palette: Palette<T>, globalPalette: GlobalPalette<T>, default: () -> T): PaletteBasedArray<T>

    fun <T : Any> of(values: VariableValueArray, globalPalette: GlobalPalette<T>): PaletteBasedArray<T> =
            of(values, globalPalette, globalPalette) { globalPalette.default }

    fun <T : Any> of(
            values: VariableValueArray, palette: Palette<T>, globalPalette: GlobalPalette<T>
    ): PaletteBasedArray<T> = of(values, palette, globalPalette) { globalPalette.default }

    fun <T : Any> of(
            values: VariableValueArray, palette: Palette<T>, globalPalette: GlobalPalette<T>, default: () -> T
    ): PaletteBasedArray<T>

    fun <T : Any> of(
            values: VariableValueArray, palette: Palette<T>, default: () -> T
    ): PaletteBasedArray<T>

    fun <T : Any> deserialize(data: SerializedPaletteBasedArray<T>, default: () -> T): PaletteBasedArray<T>

    fun <T : Any> deserialize(data: SerializedPaletteBasedArray<T>, globalPalette: GlobalPalette<T>): PaletteBasedArray<T> =
            deserialize(data, globalPalette) { globalPalette.default }

    fun <T : Any> deserialize(data: SerializedPaletteBasedArray<T>, globalPalette: GlobalPalette<T>, default: () -> T): PaletteBasedArray<T>
}

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

/**
 * Represents a serialized [PaletteBasedArray].
 */
class SerializedPaletteBasedArray<T : Any>(
        val palette: Collection<T>,
        val values: LongArray
)

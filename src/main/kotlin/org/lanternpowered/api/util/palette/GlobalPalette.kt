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
 * Represents a global palette. Should be immutable.
 */
interface GlobalPalette<T : Any> : Palette<T> {

    /**
     * The default or fallback value of the global palette.
     */
    val default: T

    override fun copy(): GlobalPalette<T> = this
}

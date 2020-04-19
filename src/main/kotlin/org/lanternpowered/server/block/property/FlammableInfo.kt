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
package org.lanternpowered.server.block.property

data class FlammableInfo(
        val encouragement: Int,
        val flammability: Int
) : Comparable<FlammableInfo> {

    override fun compareTo(other: FlammableInfo): Int {
        val result = this.encouragement.compareTo(other.encouragement)
        return if (result != 0) result else this.flammability.compareTo(other.flammability)
    }
}

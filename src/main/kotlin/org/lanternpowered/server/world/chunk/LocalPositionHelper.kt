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
package org.lanternpowered.server.world.chunk

object LocalPositionHelper {

    /*
     * A local position has three components x, y, z
     * y (4 bits) | z (4 bits) | x (4 bits)
     */

    @JvmStatic
    fun pack(localX: Int, localY: Int, localZ: Int): Short = ((localY shl 8) or (localZ shl 4) or localX).toShort()

    @JvmStatic
    fun unpackX(packed: Short): Int = packed.toInt() and 0xf

    @JvmStatic
    fun unpackZ(packed: Short): Int = (packed.toInt() shr 4) and 0xf

    @JvmStatic
    fun unpackY(packed: Short): Int = (packed.toInt() shr 8) and 0xf
}

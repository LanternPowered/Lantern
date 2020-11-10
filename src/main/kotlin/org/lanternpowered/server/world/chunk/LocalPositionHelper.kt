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

import org.spongepowered.math.vector.Vector3i

object LocalPositionHelper {

    /*
     * A local position has three components x, y, z
     * y (4 bits) | z (4 bits) | x (4 bits)
     */

    /**
     * The number of bits used to pack local positions.
     */
    val Bits = Chunks.LocalIndexBits

    @JvmStatic
    fun pack(local: Vector3i): Short = this.pack(local.x, local.y, local.z)

    @JvmStatic
    fun pack(localX: Int, localY: Int, localZ: Int): Short = ((localY shl (Chunks.Bits * 2)) or (localZ shl Chunks.Bits) or localX).toShort()

    @JvmStatic
    fun unpackX(packed: Short): Int = packed.toInt() and Chunks.Mask

    @JvmStatic
    fun unpackZ(packed: Short): Int = (packed.toInt() shr Chunks.Bits) and Chunks.Mask

    @JvmStatic
    fun unpackY(packed: Short): Int = (packed.toInt() shr (Chunks.Bits * 2)) and Chunks.Mask
}

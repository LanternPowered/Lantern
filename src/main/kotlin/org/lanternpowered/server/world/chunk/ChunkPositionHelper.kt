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

object ChunkPositionHelper {

    /*
     * A chunk position has three components x, y, z
     * x (22 bits) | z (22 bits) | y (20 bits)
     */

    const val XZBits = 22
    const val YBits = 20

    private const val YPosition = 0
    private const val YPositionLeft = Long.SIZE_BITS - YPosition
    private const val XPosition = YBits
    private const val XPositionLeft = Long.SIZE_BITS - XPosition
    private const val ZPosition = YBits + XZBits
    private const val ZPositionLeft = Long.SIZE_BITS - ZPosition

    @JvmStatic
    fun pack(x: Int, y: Int, z: Int): Long =
            (Integer.toUnsignedLong(x) shl XPosition) or
                    (Integer.toUnsignedLong(z) shl ZPosition) or Integer.toUnsignedLong(y)

    @JvmStatic
    fun unpackX(packed: Long): Int = (packed shl XPositionLeft shr XPosition).toInt()

    @JvmStatic
    fun unpackY(packed: Long): Int = (packed shl YPositionLeft shr YPosition).toInt()

    @JvmStatic
    fun unpackZ(packed: Long): Int = (packed shl ZPositionLeft shr ZPosition).toInt()
}

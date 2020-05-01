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

    @JvmStatic
    fun pack(x: Int, z: Int): Long = (Integer.toUnsignedLong(x) shl 32) or Integer.toUnsignedLong(z)

    @JvmStatic
    fun unpackX(packed: Long): Int = (packed ushr 32).toInt()

    @JvmStatic
    fun unpackZ(packed: Long): Int = (packed and 0xffffffff).toInt()
}

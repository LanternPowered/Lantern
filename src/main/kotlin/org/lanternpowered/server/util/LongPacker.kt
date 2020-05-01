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
package org.lanternpowered.server.util

object LongPacker {

    /**
     * Packs two integers into one long value.
     */
    fun pack(int1: Int, int2: Int): Long = (Integer.toUnsignedLong(int2) shl 32) or Integer.toUnsignedLong(int1)

    /**
     * Unpacks the first int value from the packed long.
     */
    fun unpackInt1(packed: Long): Int = (packed and 0xffffffffL).toInt()

    /**
     * Unpacks the second int value from the packed long.
     */
    fun unpackInt2(packed: Long): Int = (packed shr 32).toInt()
}

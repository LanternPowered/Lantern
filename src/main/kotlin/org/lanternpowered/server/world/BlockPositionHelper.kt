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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.server.world

object BlockPositionHelper {

    /**
     * A long which represents an invalid block position. All
     * the bits are set in this case.
     */
    private const val invalidBlockVectorValue = Long.MAX_VALUE or Long.MIN_VALUE

    private const val yBits = 12
    private const val xzBits = 26 // Bits per x and z value
    private const val yMask: Long = (1 shl this.yBits).toLong() - 1
    private const val xzMask: Long = (1 shl this.xzBits).toLong() - 1
    private const val yPos: Int = this.xzBits
    private const val xPos: Int = this.yPos + this.xzBits

    private const val clearYMask: Long = (this.yMask shl this.yPos).inv()
    private const val clearXMask: Long = (this.xzMask shl this.xPos).inv()
    private const val clearZMask: Long = this.xzMask

    /**
     * The bounds of the y coordinate.
     */
    const val MinYValue: Int = -(1 shl (this.yBits - 1))
    const val MaxYValue: Int = (1 shl (this.yBits - 1)) - 1

    @JvmStatic
    fun withY(packed: Long, y: Int): Long = packed and this.clearYMask or (y.toLong() shl this.yPos)

    @JvmStatic
    fun withX(packed: Long, x: Int): Long = packed and this.clearXMask or (x.toLong() shl this.xPos)

    @JvmStatic
    fun withZ(packed: Long, z: Int): Long = packed and this.clearZMask or z.toLong()

    @JvmStatic
    fun pack(x: Int, y: Int, z: Int): Long {
        // Check the bounds, and return the invalid block position in this case
        // Don't bother for x and z, we will very likely never reach the min and max values
        if (y < MinYValue || y > MaxYValue)
            return this.invalidBlockVectorValue
        return (x.toLong() and this.xzMask shl this.xPos) or (y.toLong() and this.yMask shl this.yPos) or (z.toLong() and this.xzMask)
    }

    @JvmStatic
    fun unpackX(packed: Long): Int = (packed shr this.xPos).toInt()

    @JvmStatic
    fun unpackY(packed: Long): Int = (packed shl this.yPos shr (this.yPos + this.xPos - this.yBits)).toInt()

    @JvmStatic
    fun unpackZ(packed: Long): Int = (packed shl this.xPos shr this.xPos).toInt()

    @JvmStatic
    fun isValid(packed: Long): Boolean = packed != this.invalidBlockVectorValue
}

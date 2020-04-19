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
@file:JvmName("BlockVectors")
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.world

import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i

/**
 * Shorter name alias for [BlockPosition].
 */
typealias BlockPos = BlockPosition

/**
 * A alias that is used when a [BlockVector]
 * represents a block position.
 */
typealias BlockPosition = BlockVector

/**
 * A long which represents an invalid block position. All
 * the bits are set in this case.
 */
private const val invalidBlockVectorValue = Long.MAX_VALUE or Long.MIN_VALUE

private const val yBits = 12
private const val xzBits = 26 // Bits per x and z value
private const val yMask: Long = (1 shl yBits).toLong() - 1
private const val xzMask: Long = (1 shl xzBits).toLong() - 1
private const val yPos = xzBits
private const val xPos = yPos + yBits

/**
 * Positioned bitmask within the packed value,
 * not inlined, otherwise it doesn't get optimized.
 */
private const val yPosMask = (yMask shl yPos).inv()
private const val xPosMask = (xzMask shl xPos).inv()
private const val zPosMask = xzMask

/**
 * The bounds of the y coordinate.
 */
private const val minYValue = -(1 shl (yBits - 1))
private const val maxYValue = (1 shl (yBits - 1)) - 1

private val invalidBlockVector = BlockVector(invalidBlockVectorValue)

/**
 * Alias constructor of [BlockVector].
 */
@JvmName("blockPosOf")
inline fun BlockPos(x: Int, y: Int, z: Int): BlockPos = BlockVector(x, y, z)

/**
 * Alias constructor of [BlockVector].
 */
@JvmName("blockPositionOf")
inline fun BlockPosition(x: Int, y: Int, z: Int): BlockPosition = BlockVector(x, y, z)

/**
 * Constructs a new [BlockVector] from the given x, y and z values.
 */
@JvmName("of")
fun BlockVector(x: Int, y: Int, z: Int): BlockVector {
    // Check the bounds, and return the invalid block position in this case
    // Don't bother for x and z, we will very likely never reach the min and max values
    if (y < minYValue || y > maxYValue) {
        return invalidBlockVector
    }
    return BlockVector((x.toLong() and xzMask shl xPos) or (y.toLong() and yMask shl yPos) or (z.toLong() and xzMask))
}

/**
 * Represents a block position/vector in a [World].
 */
inline class BlockVector(val value: Long) {

    /**
     * Whether this block vector is valid.
     */
    val valid: Boolean get() = this.value != invalidBlockVectorValue

    /**
     * The x value of this block vector.
     */
    val x: Int get() = (this.value shr xPos).toInt()

    /**
     * The y value of this block vector.
     */
    val y: Int get() = (this.value shl yPos shr (yPos + xPos - yBits)).toInt()

    /**
     * The z value of this block vector.
     */
    val z: Int get() = (this.value shl xPos shr xPos).toInt()

    inline operator fun component1(): Int = x
    inline operator fun component2(): Int = y
    inline operator fun component3(): Int = z

    private fun withY(y: Int): BlockVector = BlockVector(this.value and yPosMask or (y.toLong() shl yPos))
    private fun withX(x: Int): BlockVector = BlockVector(this.value and xPosMask or (x.toLong() shl xPos))
    private fun withZ(z: Int): BlockVector = BlockVector(this.value and zPosMask or z.toLong())

    fun down(): BlockVector = down(1)
    fun down(offset: Int): BlockVector = withY(this.y - offset)

    fun up(): BlockVector = up(1)
    fun up(offset: Int): BlockVector = withY(this.y + offset)

    fun north(): BlockVector = north(1)
    fun north(offset: Int): BlockVector = withZ(this.z - offset)

    fun south(): BlockVector = south(1)
    fun south(offset: Int): BlockVector = withZ(this.z + offset)

    fun west(): BlockVector = west(1)
    fun west(offset: Int): BlockVector = withX(this.x - offset)

    fun east(): BlockVector = east(1)
    fun east(offset: Int): BlockVector = withX(this.x + offset)

    /**
     * Adds the x, y and z values to this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    fun add(x: Int, y: Int, z: Int) = BlockVector(this.x + x, this.y + y, this.z + z)

    /**
     * Adds the [Vector3i] values to this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun plus(v: Vector3i) = add(v.x, v.y, v.z)

    /**
     * Adds the [Vector3i] values to this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun plus(v: BlockVector) = add(v.x, v.y, v.z)

    /**
     * Subtracts the x, y and z values from this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    fun sub(x: Int, y: Int, z: Int) = BlockVector(this.x - x, this.y - y, this.z - z)

    /**
     * Subtracts the [Vector3i] values from this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun minus(v: Vector3i) = sub(v.x, v.y, v.z)

    /**
     * Subtracts the [Vector3i] values from this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun minus(v: BlockVector) = sub(v.x, v.y, v.z)

    /**
     * Multiplies the x, y and z values with this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    fun mul(x: Int, y: Int, z: Int) = BlockVector(this.x * x, this.y * y, this.z * z)

    /**
     * Multiplies the [Vector3i] values with this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun times(v: Vector3i) = mul(v.x, v.y, v.z)

    /**
     * Multiplies the [Vector3i] values with this [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun times(v: BlockVector) = mul(v.x, v.y, v.z)

    /**
     * Divides the [BlockVector] values with the x, y and z and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    fun div(x: Int, y: Int, z: Int) = BlockVector(this.x / x, this.y / y, this.z / z)

    /**
     * Divides the [BlockVector] values with the [Vector3i] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun div(v: Vector3i) = div(v.x, v.y, v.z)

    /**
     * Divides the [BlockVector] values with the [BlockVector] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun div(v: BlockVector) = div(v.x, v.y, v.z)

    /**
     * Negates this [BlockVector].
     */
    operator fun unaryMinus() = BlockVector(-this.x, -this.y, -this.z)

    /**
     * Just returns this [BlockVector].
     */
    operator fun unaryPlus() = this

    /**
     * Converts this [BlockVector] into a [Vector3i].
     */
    fun toVector3i(): Vector3i = Vector3i.from(this.x, this.y, this.z)

    /**
     * Converts this [BlockVector] into a [Vector3d].
     */
    fun toVector3d(): Vector3d = Vector3d.from(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())

    override fun toString() = "($x, $y, $z)"
}

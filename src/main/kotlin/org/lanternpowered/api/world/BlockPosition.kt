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

package org.lanternpowered.api.world

import org.lanternpowered.server.world.BlockPositionHelper
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i

/**
 * Represents a block position in a [World].
 */
inline class BlockPosition @Deprecated(message = "Internal use only.", level = DeprecationLevel.WARNING) constructor(private val packed: Long) {

    /**
     * Constructs a new [BlockPosition] from the given x, y and z values.
     *
     * The constructed position can be invalid if the y coordinate is outside
     * the range of supported values.
     */
    constructor(x: Int, y: Int, z: Int) : this(BlockPositionHelper.pack(x, y, z))

    /**
     * Whether this block vector is valid.
     */
    val valid: Boolean get() = BlockPositionHelper.isValid(this.packed)

    /**
     * The x value of this block vector.
     */
    val x: Int get() = BlockPositionHelper.unpackX(this.packed)

    /**
     * The y value of this block vector.
     */
    val y: Int get() = BlockPositionHelper.unpackY(this.packed)

    /**
     * The z value of this block vector.
     */
    val z: Int get() = BlockPositionHelper.unpackZ(this.packed)

    inline operator fun component1(): Int = this.x
    inline operator fun component2(): Int = this.y
    inline operator fun component3(): Int = this.z

    fun west(): BlockPosition = west(1)
    fun west(offset: Int): BlockPosition = BlockPosition(BlockPositionHelper.withX(this.packed, this.x - offset))

    fun east(): BlockPosition = east(1)
    fun east(offset: Int): BlockPosition = west(-offset)

    fun down(): BlockPosition = down(1)
    fun down(offset: Int): BlockPosition = BlockPosition(BlockPositionHelper.withY(this.packed, this.y - offset))

    fun up(): BlockPosition = up(1)
    fun up(offset: Int): BlockPosition = down(-offset)

    fun north(): BlockPosition = north(1)
    fun north(offset: Int): BlockPosition = BlockPosition(BlockPositionHelper.withZ(this.packed, this.z - offset))

    fun south(): BlockPosition = south(1)
    fun south(offset: Int): BlockPosition = north(-offset)

    /**
     * Adds the x, y and z values to this [BlockPosition] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    fun add(x: Int, y: Int, z: Int) = BlockPosition(this.x + x, this.y + y, this.z + z)

    /**
     * Adds the [Vector3i] values to this [BlockPosition] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun plus(v: Vector3i) = add(v.x, v.y, v.z)

    /**
     * Subtracts the x, y and z values from this [BlockPosition] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    fun sub(x: Int, y: Int, z: Int) = BlockPosition(this.x - x, this.y - y, this.z - z)

    /**
     * Subtracts the [Vector3i] values from this [BlockPosition] and returns a new vector.
     * <p>Ignores the fact that one of the vectors could be invalid.
     */
    operator fun minus(v: Vector3i) = sub(v.x, v.y, v.z)

    /**
     * Converts this [BlockPosition] into a [BlockPosition].
     */
    fun toVector3i(): Vector3i = Vector3i.from(this.x, this.y, this.z)

    /**
     * Converts this [BlockPosition] into a [BlockPosition].
     */
    fun toVector3d(): Vector3d = Vector3d.from(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())

    /**
     * Converts this position to a string representation.
     */
    override fun toString() = "($x, $y, $z)"

    companion object {

        /**
         * The maximum value of the y component.
         */
        const val MaxYValue = BlockPositionHelper.MaxYValue

        /**
         * The minimum value of the y component.
         */
        const val MinYValue = BlockPositionHelper.MinYValue
    }
}

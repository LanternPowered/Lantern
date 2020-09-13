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
package org.lanternpowered.api.world.chunk

import org.lanternpowered.server.world.chunk.ChunkPositionHelper
import org.spongepowered.math.vector.Vector3i

/**
 * Represents a position of a chunk.
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class ChunkPosition @PublishedApi internal constructor(
        internal val packed: Long
) {

    /**
     * Constructs a new [ChunkPosition] from the given x, y and z values.
     */
    constructor(x: Int, y: Int, z: Int) : this(ChunkPositionHelper.pack(x, y, z))

    /**
     * The x coordinate.
     */
    val x: Int
        get() = ChunkPositionHelper.unpackX(this.packed)

    /**
     * The y coordinate.
     */
    val y: Int
        get() = ChunkPositionHelper.unpackY(this.packed)

    /**
     * The z coordinate.
     */
    val z: Int
        get() = ChunkPositionHelper.unpackZ(this.packed)

    fun offset(xOffset: Int, yOffset: Int, zOffset: Int): ChunkPosition =
            ChunkPosition(this.x + xOffset, this.y + yOffset, this.z + zOffset)

    fun west(): ChunkPosition = this.west(1)
    fun west(offset: Int): ChunkPosition = this.offset(-offset, 0, 0)

    fun east(): ChunkPosition = this.east(1)
    fun east(offset: Int): ChunkPosition = this.offset(offset, 0, 0)

    fun north(): ChunkPosition = this.north(1)
    fun north(offset: Int): ChunkPosition = this.offset(0, 0, -offset)

    fun south(): ChunkPosition = this.south(1)
    fun south(offset: Int): ChunkPosition = this.offset(0, 0, offset)

    fun up(): ChunkPosition = this.up(1)
    fun up(offset: Int): ChunkPosition = this.offset(0, offset, 0)

    fun down(): ChunkPosition = this.down(1)
    fun down(offset: Int): ChunkPosition = this.offset(0, -offset, 0)

    fun toVector(): Vector3i = Vector3i(this.x, this.y, this.z)

    override fun toString(): String = "($x, $y, $z)"
}

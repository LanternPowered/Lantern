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

/**
 * Represents a position of a chunk.
 */
inline class ChunkPosition @Deprecated(message = "Internal use only.", level = DeprecationLevel.WARNING) constructor(
        @Deprecated(message = "Internal use only.", level = DeprecationLevel.WARNING) val packed: Long
) {

    /**
     * Constructs a new [ChunkPosition] from the given x and z values.
     */
    constructor(x: Int, z: Int) : this(ChunkPositionHelper.pack(x, z))

    /**
     * The x coordinate.
     */
    val x: Int
        get() = ChunkPositionHelper.unpackX(this.packed)

    /**
     * The z coordinate.
     */
    val z: Int
        get() = ChunkPositionHelper.unpackZ(this.packed)

    fun offset(xOffset: Int, zOffset: Int): ChunkPosition = ChunkPosition(this.x + xOffset, this.z + zOffset)

    fun west(): ChunkPosition = west(1)
    fun west(offset: Int): ChunkPosition = offset(-offset, 0)

    fun east(): ChunkPosition = east(1)
    fun east(offset: Int): ChunkPosition = offset(offset, 0)

    fun north(): ChunkPosition = north(1)
    fun north(offset: Int): ChunkPosition = offset(0, -offset)

    fun south(): ChunkPosition = south(1)
    fun south(offset: Int): ChunkPosition = offset(0, offset)

    override fun toString(): String = "($x, $z)"
}

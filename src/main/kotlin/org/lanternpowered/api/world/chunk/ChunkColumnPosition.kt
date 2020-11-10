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

package org.lanternpowered.api.world.chunk

import org.lanternpowered.server.world.chunk.ChunkPositionHelper
import org.spongepowered.math.vector.Vector2i

/**
 * Represents a position of a chunk column.
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class ChunkColumnPosition @PublishedApi internal constructor(
        internal val packed: Long
) {

    /**
     * Constructs a new [ChunkColumnPosition] from the given x and z values.
     */
    constructor(x: Int, z: Int) : this(ChunkPositionHelper.pack(x, 0, z))

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

    inline operator fun component1(): Int = this.x
    inline operator fun component2(): Int = this.z

    fun offset(xOffset: Int, zOffset: Int): ChunkColumnPosition =
            ChunkColumnPosition(this.x + xOffset, this.z + zOffset)

    fun west(): ChunkColumnPosition = this.west(1)
    fun west(offset: Int): ChunkColumnPosition = this.offset(-offset, 0)

    fun east(): ChunkColumnPosition = this.east(1)
    fun east(offset: Int): ChunkColumnPosition = this.offset(offset, 0)

    fun north(): ChunkColumnPosition = this.north(1)
    fun north(offset: Int): ChunkColumnPosition = this.offset(0, -offset)

    fun south(): ChunkColumnPosition = this.south(1)
    fun south(offset: Int): ChunkColumnPosition = this.offset(0, offset)

    fun toVector(): Vector2i = Vector2i(this.x, this.z)

    override fun toString(): String = "($x, $z)"
}

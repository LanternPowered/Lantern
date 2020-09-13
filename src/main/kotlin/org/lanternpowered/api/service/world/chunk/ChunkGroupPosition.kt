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
package org.lanternpowered.api.service.world.chunk

import org.lanternpowered.server.world.chunk.ChunkPositionHelper

/**
 * Represents a position of a chunk group.
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class ChunkGroupPosition private constructor(private val packed: Long) {

    /**
     * Constructs a new [ChunkGroupPosition] from the given x, y and z values.
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

    override fun toString(): String = "($x, $y, $z)"
}

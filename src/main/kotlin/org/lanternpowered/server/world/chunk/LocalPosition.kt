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

import org.spongepowered.math.vector.Vector3i

/**
 * Represents a local position of a block in a chunk.
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class LocalPosition @PublishedApi internal constructor(
        internal val packedShort: Short
) {

    /**
     * Constructs a new [LocalPosition] from the given x, y and z values.
     */
    constructor(x: Int, y: Int, z: Int) : this(LocalPositionHelper.pack(x, y, z))

    /**
     * The packed as an int.
     */
    internal inline val packed: Int
        get() = this.packedShort.toInt()

    /**
     * The x coordinate.
     */
    val x: Int
        get() = LocalPositionHelper.unpackX(this.packedShort)

    /**
     * The y coordinate.
     */
    val y: Int
        get() = LocalPositionHelper.unpackY(this.packedShort)

    /**
     * The z coordinate.
     */
    val z: Int
        get() = LocalPositionHelper.unpackZ(this.packedShort)

    fun toVector(): Vector3i = Vector3i(this.x, this.y, this.z)

    override fun toString(): String = "($x, $y, $z)"
}

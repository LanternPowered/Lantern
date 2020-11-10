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

package org.lanternpowered.server.world.chunk

import org.lanternpowered.api.world.chunk.ChunkColumnPosition
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.spongepowered.math.vector.Vector3i

internal object Chunks {

    /**
     * The amount of bits used by a chunk.
     */
    const val Bits = 4

    /**
     * The 1D size of a chunk. (x, y and z)
     */
    const val Size = 1 shl Bits

    /**
     * The mask and maximum index of [Size].
     */
    const val Mask = Size - 1

    /**
     * Half of [Size].
     */
    const val HalfSize = Size / 2

    /**
     * The 2D area of a single chunk. (x * z)
     */
    const val Area = Size * Size

    /**
     * The 3D volume of a single chunk. (x * y * z)
     */
    const val Volume = Size * Size * Size

    const val LocalIndexBits = Bits * 3
    const val LocalIndexMax = (1 shl LocalIndexBits) - 1

    /**
     * The minimum chunk x and z coordinate.
     */
    const val MinXZ = -1875000

    /**
     * The maximum chunk x and z coordinate.
     */
    const val MaxXZ = 1875000

    /**
     * The minimum chunk y coordinate.
     */
    const val MinY = 0

    /**
     * The maximum chunk y coordinate.
     */
    const val MaxY = 15

    /**
     * The range of chunk y coordinates.
     */
    val RangeY = MinY..MaxY

    const val MinBlockXZ = MinXZ shl Bits
    const val MaxBlockXZ = MaxXZ shl Bits - 1
    const val MinBlockY = MinY shl Bits
    const val MaxBlockY = MaxY shl Bits - 1

    val LocalRange = 0 until Size

    /**
     * Converts the x, y or z component to a chunk component coordinate.
     */
    inline fun toChunk(component: Int): Int = component shr Bits

    /**
     * Converts the x, y, z components to chunk coordinates.
     */
    inline fun toChunk(x: Int, y: Int, z: Int): ChunkPosition =
            ChunkPosition(this.toChunk(x), this.toChunk(y), this.toChunk(z))

    /**
     * Converts the x, y, z components to chunk coordinates.
     */
    fun toChunk(position: Vector3i): ChunkPosition =
            this.toChunk(position.x, position.y, position.z)

    /**
     * Converts the x, y or z component to chunk column coordinates.
     */
    inline fun toChunkColumn(x: Int, z: Int): ChunkColumnPosition =
            ChunkColumnPosition(this.toChunk(x), this.toChunk(z))

    /**
     * Converts the x, y or z component to a local component coordinate.
     */
    inline fun toLocal(component: Int): Int = component and Mask

    /**
     * Converts the x, y, z components to local coordinates.
     */
    fun toLocal(x: Int, y: Int, z: Int): LocalPosition =
            LocalPosition(this.toLocal(x), this.toLocal(y), this.toLocal(z))

    /**
     * Converts the x, y, z components to local coordinates.
     */
    fun toLocal(position: Vector3i): LocalPosition =
            this.toLocal(position.x, position.y, position.z)

    /**
     * Converts the x, y or z component to a global component coordinate.
     */
    inline fun toGlobal(chunkComponent: Int, localComponent: Int): Int = (chunkComponent shl Bits) or localComponent

    /**
     * Converts the x, y or z component to a global component coordinate.
     */
    inline fun toGlobal(chunkComponent: Int): Int = chunkComponent shl Bits

    /**
     * Converts the x, y, z to global coordinates.
     */
    fun toGlobal(chunk: ChunkPosition): Vector3i =
            this.toGlobal(chunk.x, chunk.y, chunk.z)

    /**
     * Converts the x, y, z to global coordinates.
     */
    fun toGlobal(chunkX: Int, chunkY: Int, chunkZ: Int): Vector3i {
        val x = this.toGlobal(chunkX)
        val y = this.toGlobal(chunkY)
        val z = this.toGlobal(chunkZ)
        return Vector3i(x, y, z)
    }

    /**
     * Converts the x, y, z to global coordinates.
     */
    fun toGlobal(chunk: ChunkPosition, local: LocalPosition): Vector3i =
            this.toGlobal(chunk.x, chunk.y, chunk.z, local.x, local.y, local.z)

    /**
     * Converts the x, y, z to global coordinates.
     */
    fun toGlobal(chunk: ChunkPosition, localX: Int, localY: Int, localZ: Int): Vector3i =
            this.toGlobal(chunk.x, chunk.y, chunk.z, localX, localY, localZ)

    /**
     * Converts the x, y, z to global coordinates.
     */
    fun toGlobal(chunkX: Int, chunkY: Int, chunkZ: Int, localX: Int, localY: Int, localZ: Int): Vector3i {
        val x = this.toGlobal(chunkX, localX)
        val y = this.toGlobal(chunkY, localY)
        val z = this.toGlobal(chunkZ, localZ)
        return Vector3i(x, y, z)
    }

    /**
     * Converts the local x, y and z components into a index (4 bits per component, so 12 bits). Therefore
     * the value ranges between 0 and (1 shl 12) - 1.
     */
    fun localIndex(localX: Int, localY: Int, localZ: Int): Int = (localY shl 8) or (localZ shl 4) or localX

    /**
     * Performs the given [function] for each [LocalPosition] of a chunk.
     */
    inline fun forEachLocalPosition(function: (LocalPosition) -> Unit) {
        for (i in 0 until Volume)
            function(LocalPosition(i))
    }
}

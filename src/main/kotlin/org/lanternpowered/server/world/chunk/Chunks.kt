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

import org.lanternpowered.api.world.chunk.ChunkPosition
import org.spongepowered.math.vector.Vector3i

object Chunks {

    const val Bits = 4
    const val Size = 1 shl Bits
    const val Mask = Size - 1

    const val LocalIndexBits = Bits * 3
    const val LocalIndexMax = (1 shl LocalIndexBits) - 1

    /**
     * Converts the x, y or z component to a chunk component coordinate.
     */
    inline fun toChunk(component: Int): Int = component shr Bits

    /**
     * Converts the x, y or z component to a local component coordinate.
     */
    inline fun toLocal(component: Int): Int = component and Mask

    /**
     * Converts the x, y, z components to local coordinates.
     */
    fun toLocalVector(x: Int, y: Int, z: Int): Vector3i =
            Vector3i(this.toLocal(x), this.toLocal(y), this.toLocal(z))

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
    fun toGlobal(chunk: ChunkPosition, localX: Int, localY: Int, localZ: Int): Vector3i {
        val x = this.toGlobal(chunk.x, localX)
        val y = this.toGlobal(chunk.y, localY)
        val z = this.toGlobal(chunk.z, localZ)
        return Vector3i(x, y, z)
    }

    /**
     * Converts the local x, y and z components into a index (4 bits per component, so 12 bits). Therefore
     * the value ranges between 0 and (1 shl 12) - 1.
     */
    fun localIndex(localX: Int, localY: Int, localZ: Int): Int = (localX shl 8) or (localZ shl 4) or localY
}

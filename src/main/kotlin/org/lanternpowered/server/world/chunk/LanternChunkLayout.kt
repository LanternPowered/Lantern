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

import org.spongepowered.api.world.storage.ChunkLayout
import org.spongepowered.math.vector.Vector3i

object LanternChunkLayout : ChunkLayout {

    const val MinChunkXZ = -1875000
    const val MaxChunkXZ = 1875000
    const val MinChunkY = 0
    const val MaxChunkY = 16

    const val MinBlockXZ = MinChunkXZ shl Chunks.Bits
    const val MaxBlockXZ = MaxChunkXZ shl Chunks.Bits - 1
    const val MinBlockY = MinChunkY shl Chunks.Bits
    const val MaxBlockY = MaxChunkY shl Chunks.Bits - 1

    private val spaceMax = Vector3i(Chunks.MaxBlockXZ, Chunks.MaxBlockY, Chunks.MaxBlockXZ)
    private val spaceMin = Vector3i(Chunks.MinBlockXZ, Chunks.MinBlockY, Chunks.MinBlockXZ)
    private val spaceSize = this.spaceMax.sub(this.spaceMin).add(1, 1, 1)

    private val chunkSize = Vector3i(Chunks.Size, Chunks.Size, Chunks.Size)

    val chunkColumnSize: Vector3i = this.chunkSize.mul(Chunks.MaxY - Chunks.MinY + 1)

    override fun getSpaceMax(): Vector3i = this.spaceMax
    override fun getSpaceMin(): Vector3i = this.spaceMin
    override fun getSpaceSize(): Vector3i = this.spaceSize
    override fun getSpaceOrigin(): Vector3i = Vector3i.ZERO

    override fun getChunkSize(): Vector3i = this.chunkSize

    private inline fun isInChunk(component: Int) = component and Chunks.Mask.inv() == 0

    override fun isInChunk(x: Int, y: Int, z: Int): Boolean =
            this.isInChunk(x) && this.isInChunk(y) && this.isInChunk(z)

    override fun isInChunk(wx: Int, wy: Int, wz: Int, cx: Int, cy: Int, cz: Int): Boolean =
            this.isInChunk(wx - Chunks.toGlobal(cx), wy - Chunks.toGlobal(cy), wz - Chunks.toGlobal(cz))

    override fun forceToWorld(x: Int, y: Int, z: Int): Vector3i = Chunks.toGlobal(x, y, z)
    override fun forceToChunk(x: Int, y: Int, z: Int): Vector3i = Chunks.toLocal(x, y, z).toVector()
}

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

    private const val MaxSize = 30000000
    private const val MaxHeight = Chunks.Size * 16 - 1 /* 16 chunks in vanilla. */
    private const val MinHeight = 0

    private val spaceMax = Vector3i(MaxSize, MaxHeight, MaxSize)
    private val spaceMin = Vector3i(MaxSize, MinHeight, MaxSize).negate()
    private val spaceSize = this.spaceMax.sub(this.spaceMin).add(1, 1, 1)

    private val chunkSize = Vector3i(Chunks.Size, Chunks.Size, Chunks.Size)

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

    override fun forceToWorld(x: Int, y: Int, z: Int): Vector3i = Chunks.toLocalVector(x, y, z)
    override fun forceToChunk(x: Int, y: Int, z: Int): Vector3i = Chunks.toLocalVector(x, y, z)
}

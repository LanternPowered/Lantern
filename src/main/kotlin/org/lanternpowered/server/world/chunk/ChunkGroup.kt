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

import org.lanternpowered.api.service.world.chunk.ChunkGroupPosition
import org.lanternpowered.api.service.world.chunk.ChunkStorage
import org.lanternpowered.api.util.math.floorToInt
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.spongepowered.math.vector.Vector3i

/**
 * Represents a group of chunks which have to be managed together because
 * they are saved together. The size of a group will be determined by the
 * [ChunkStorage] this chunk belongs to.
 */
class ChunkGroup {

}

internal fun ChunkStorage.getChunkGroupPosition(position: Vector3i): ChunkGroupPosition =
        this.getChunkGroupPosition(position.x, position.y, position.z)

internal fun ChunkStorage.getChunkGroupPosition(position: ChunkPosition): ChunkGroupPosition =
        this.getChunkGroupPosition(position.x, position.y, position.z)

internal fun ChunkStorage.getChunkGroupPosition(x: Int, y: Int, z: Int): ChunkGroupPosition {
    val size = this.groupSize
    val groupX = (x.toDouble() / size.x).floorToInt()
    val groupY = (y.toDouble() / size.y).floorToInt()
    val groupZ = (z.toDouble() / size.z).floorToInt()
    return ChunkGroupPosition(groupX, groupY, groupZ)
}

internal fun ChunkStorage.getLocalChunkPosition(position: Vector3i): ChunkPosition =
        this.getLocalChunkPosition(position.x, position.y, position.z)

internal fun ChunkStorage.getLocalChunkPosition(position: ChunkPosition): ChunkPosition =
        this.getLocalChunkPosition(position.x, position.y, position.z)

internal fun ChunkStorage.getLocalChunkPosition(x: Int, y: Int, z: Int): ChunkPosition {
    val size = this.groupSize
    val group = this.getChunkGroupPosition(x, y, z)
    val localX = x - size.x * group.x
    val localY = y - size.y * group.y
    val localZ = z - size.z * group.z
    return ChunkPosition(localX, localY, localZ)
}

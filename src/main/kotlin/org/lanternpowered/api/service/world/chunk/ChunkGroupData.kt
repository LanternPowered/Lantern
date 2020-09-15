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

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.world.chunk.ChunkPosition

/**
 * Represents the data of a chunk group.
 */
interface ChunkGroupData {

    /**
     * Gets the data of a chunk using the local chunk coordinates.
     */
    operator fun get(x: Int, y: Int, z: Int): DataContainer

    /**
     * Gets the data of a chunk using the local chunk coordinates.
     */
    operator fun get(position: ChunkPosition): DataContainer = this[position.x, position.y, position.z]
}

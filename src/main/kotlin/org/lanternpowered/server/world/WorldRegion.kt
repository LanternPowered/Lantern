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
package org.lanternpowered.server.world

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import org.lanternpowered.api.world.chunk.ChunkPosition

/**
 * Represents a region of a world, which is basically a set
 * of chunks that are neighbors of each other.
 *
 * Inspired by: https://github.com/PaperMC/Paper/issues/1001
 */
class WorldRegion {

    /**
     * All the chunks in this region.
     */
    val chunks = Long2ObjectOpenHashMap<LanternChunk>()

    /**
     * A set with all the edge chunk coordinates. An edge chunk isn't
     * surrounded by its 4 neighboring chunks.
     */
    val edge = LongOpenHashSet()

    fun update() {

    }

    fun add(chunk: LanternChunk) {
        this.chunks[chunk.position.packed] = chunk
        chunk.region = this
    }

    fun remove(chunk: LanternChunk) {
        this.chunks.remove(chunk.position.packed)
        chunk.region = null
    }

    fun has(position: ChunkPosition): Boolean =
            this.chunks.containsKey(position.packed)

    fun hasNeighbor(position: ChunkPosition): Boolean =
            has(position.east()) || has(position.west()) || has(position.north()) || has(position.south())
}

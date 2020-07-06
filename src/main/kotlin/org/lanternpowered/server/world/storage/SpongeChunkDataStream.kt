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
package org.lanternpowered.server.world.storage

import org.lanternpowered.api.service.world.ChunkStorage
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.world.storage.ChunkDataStream

/**
 * A sponge chunk data stream that wraps around the [ChunkStorage] data stream.
 */
class SpongeChunkDataStream(
        private val chunkStorage: ChunkStorage
) : ChunkDataStream {

    private var sequence = this.chunkStorage.sequence().iterator()
    private var read: Int = 0
    private var available: Int = -1

    override fun next(): DataContainer? {
        val entry = this.sequence.next()
        try {
            return entry.load()
        } finally {
            this.read++
        }
    }

    override fun available(): Int {
        // This will be an estimate, the stream can still change
        // if chunks are being saved in the meantime
        if (this.available == -1)
            this.available = this.chunkStorage.sequence().count()
        return this.available - this.read
    }

    override fun hasNext(): Boolean = this.sequence.hasNext()

    override fun reset() {
        // Restart the sequence
        this.sequence = this.chunkStorage.sequence().iterator()
        this.available = -1
        this.read = 0
    }
}

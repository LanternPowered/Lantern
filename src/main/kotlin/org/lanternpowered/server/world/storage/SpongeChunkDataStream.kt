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

import org.lanternpowered.api.service.world.chunk.ChunkStorage
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.world.storage.ChunkDataStream

/**
 * A sponge chunk data stream that wraps around the [ChunkStorage] data stream.
 */
class SpongeChunkDataStream(
        private val chunkStorage: ChunkStorage
) : ChunkDataStream {

    private fun sequence(): Iterator<DataContainer> = this.chunkStorage.sequence()
            .flatMap { entry ->
                val size = this.chunkStorage.groupSize
                val data = entry.load()
                        ?: return@flatMap emptySequence<DataContainer>()
                sequence {
                    for (localX in 0 until size.x) {
                        for (localY in 0 until size.y) {
                            for (localZ in 0 until size.z)
                                yield(data[localX, localY, localZ])
                        }
                    }
                }
            }
            .iterator()

    private var sequence = this.sequence()

    private var read = 0
    private var available = UNKNOWN

    override fun next(): DataContainer? {
        val entry = this.sequence.next()
        try {
            return entry
        } finally {
            this.read++
        }
    }

    override fun available(): Int {
        // This will be an estimate, the stream can still change
        // if chunks are being saved in the meantime
        if (this.available == UNKNOWN) {
            val size = this.chunkStorage.groupSize
            this.available = this.chunkStorage.sequence().count() * (size.x * size.y * size.z)
        }
        return this.available - this.read
    }

    override fun hasNext(): Boolean = this.sequence.hasNext()

    override fun reset() {
        // Restart the sequence
        this.sequence = this.sequence()
        this.available = UNKNOWN
        this.read = 0
    }

    companion object {

        private const val UNKNOWN = -1
    }
}

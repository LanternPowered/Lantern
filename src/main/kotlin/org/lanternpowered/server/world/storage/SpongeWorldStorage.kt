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

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.world.WorldProperties
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.spongepowered.api.world.storage.ChunkDataStream
import org.spongepowered.math.vector.Vector3i
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

/**
 * A sponge world storage that wraps around the lantern [WorldStorage].
 */
class SpongeWorldStorage(
        private val executorService: ExecutorService,
        private val properties: WorldProperties,
        private val worldStorage: WorldStorage
) : org.spongepowered.api.world.storage.WorldStorage {

    override fun getWorldProperties(): WorldProperties = this.properties

    override fun doesChunkExist(chunkCoords: Vector3i): CompletableFuture<Boolean> = CompletableFuture.supplyAsync(
            Supplier { this.worldStorage.chunks.exists(ChunkPosition(chunkCoords.x, chunkCoords.z)) }, this.executorService)

    override fun getChunkData(chunkCoords: Vector3i): CompletableFuture<Optional<DataContainer>> = CompletableFuture.supplyAsync(
            Supplier { this.worldStorage.chunks.load(ChunkPosition(chunkCoords.x, chunkCoords.z)).optional() }, this.executorService)

    override fun getGeneratedChunks(): ChunkDataStream = SpongeChunkDataStream(this.worldStorage.chunks)
}

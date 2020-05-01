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
package org.lanternpowered.server.service.world.anvil

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.service.world.ChunkStorage
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

class AnvilChunkStorage(private val executorService: ExecutorService, worldDirectory: Path) : ChunkStorage {

    private val anvilRegionFileCache = AnvilRegionFileCache(worldDirectory)

    override fun exists(position: ChunkPosition): CompletableFuture<Boolean> =
            CompletableFuture.supplyAsync(Supplier { this.anvilRegionFileCache.exists(position) }, this.executorService)

    override fun save(position: ChunkPosition, chunkData: DataView): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync(Supplier {
            val output = this.anvilRegionFileCache.getOutputStream(position)
            val data = fixSavedData(position, chunkData)
            NbtStreamUtils.write(data, output, false)
        }, this.executorService)
    }

    override fun load(position: ChunkPosition): CompletableFuture<DataContainer?> {
        return CompletableFuture.supplyAsync(Supplier {
            val input = this.anvilRegionFileCache.getInputStream(position)
                    ?: return@Supplier null
            val data = NbtStreamUtils.read(input, false)
            fixLoadedData(data)
        }, this.executorService)
    }

    override fun sequence(): Sequence<ChunkStorage.Entry> {
        return this.anvilRegionFileCache.sequence()
                .flatMap { file ->
                    file.all.asSequence().map { position -> Entry(position) }
                }
    }

    private inner class Entry(override val position: ChunkPosition) : ChunkStorage.Entry {
        override fun load(): CompletableFuture<DataContainer> = load(this.position)
                .thenApplyAsync { container -> container ?: throw IllegalStateException("The data for chunk $position is no longer available.") }
    }

    private fun fixLoadedData(data: DataContainer): DataContainer {
        data.remove(X_POS)
        data.remove(Z_POS)
        return data
    }

    private fun fixSavedData(position: ChunkPosition, data: DataView): DataView {
        @Suppress("NAME_SHADOWING")
        val data = data.copy()
        data.set(X_POS, position.x)
        data.set(Z_POS, position.z)
        return data
    }

    companion object {
        val X_POS: DataQuery = DataQuery.of("Level", "xPos")
        val Z_POS: DataQuery = DataQuery.of("Level", "zPos")
    }
}

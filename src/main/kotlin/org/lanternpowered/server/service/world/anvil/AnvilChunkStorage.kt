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

class AnvilChunkStorage(worldDirectory: Path) : ChunkStorage {

    @Volatile private var closed = false
    private val anvilRegionFileCache = AnvilRegionFileCache(worldDirectory)

    private fun checkOpen() {
        check(!this.closed) { "The chunk storage is closed." }
    }

    override fun exists(position: ChunkPosition): Boolean {
        this.checkOpen()
        return this.anvilRegionFileCache.exists(position)
    }

    override fun delete(position: ChunkPosition): Boolean {
        this.checkOpen()
        return this.anvilRegionFileCache.delete(position)
    }

    override fun save(position: ChunkPosition, chunkData: DataView) {
        this.checkOpen()
        val output = this.anvilRegionFileCache.getOutputStream(position)
        val data = this.fixSavedData(position, chunkData)
        NbtStreamUtils.write(data, output, false)
    }

    override fun load(position: ChunkPosition): DataContainer? {
        this.checkOpen()
        val input = this.anvilRegionFileCache.getInputStream(position) ?: return null
        val data = NbtStreamUtils.read(input, false)
        return this.fixLoadedData(data)
    }

    override fun sequence(): Sequence<ChunkStorage.Entry> {
        this.checkOpen()
        return this.anvilRegionFileCache.sequence()
                .flatMap { file ->
                    file.all.asSequence().map { position -> Entry(position) }
                }
    }

    private inner class Entry(override val position: ChunkPosition) : ChunkStorage.Entry {
        override fun load(): DataContainer =
                load(this.position) ?: throw IllegalStateException("The data for chunk $position is no longer available.")
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

    fun close() {
        if (this.closed)
            return
        this.closed = true
        this.anvilRegionFileCache.clear()
    }

    companion object {
        val X_POS: DataQuery = DataQuery.of("Level", "xPos")
        val Z_POS: DataQuery = DataQuery.of("Level", "zPos")
    }
}

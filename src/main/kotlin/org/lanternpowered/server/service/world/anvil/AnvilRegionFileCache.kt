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

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalListener
import org.lanternpowered.api.world.chunk.ChunkPosition
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class AnvilRegionFileCache(worldDirectory: Path) {

    private val directory = worldDirectory.resolve(REGION_DIRECTORY_NAME)
    private val cache: Cache<Long, AnvilRegionFile> = Caffeine.newBuilder()
            .maximumSize(256)
            .softValues()
            .removalListener(RemovalListener<Long, AnvilRegionFile> { _, value: AnvilRegionFile?, _ ->
                value?.close()
            })
            .build()

    init {
        if (!Files.exists(this.directory))
            Files.createDirectories(this.directory)
    }

    /**
     * Gets a lazy sequence of all the [AnvilRegionFile]s.
     */
    fun sequence(): Sequence<AnvilRegionFile> = Files.walk(this.directory)
            .asSequence()
            .map { path ->
                val result = REGION_FILE_PATTERN.find(path.fileName.toString()) ?: return@map null
                val regionX = result.groupValues[1].toInt()
                val regionZ = result.groupValues[2].toInt()
                get(ChunkRegionPosition(regionX, regionZ))
            }
            .filterNotNull()

    /**
     * Gets the [AnvilRegionFile] for the given region coordinates.
     */
    fun get(position: ChunkRegionPosition): AnvilRegionFile {
        return this.cache.get(position.packed) {
            AnvilRegionFile(position, position.getFile())
        }!!
    }

    /**
     * Gets the [AnvilRegionFile] for the given region coordinates if it exists.
     */
    fun getIfPresent(position: ChunkRegionPosition): AnvilRegionFile? {
        val regionFile = this.cache.getIfPresent(position.packed)
        if (regionFile != null)
            return regionFile
        val file = position.getFile()
        if (!Files.exists(file))
            return null
        return this.cache.get(position.packed) { AnvilRegionFile(position, file) }
    }

    private fun ChunkRegionPosition.getFile(): Path = directory.resolve("r.$x.$z.${AnvilRegionFile.REGION_FILE_EXTENSION}")

    /**
     * Gets whether the chunk at the given [ChunkPosition] exists.
     */
    fun exists(position: ChunkPosition): Boolean =
            getIfPresent(position.toRegion())?.exists(position) ?: false

    /**
     * Gets an input stream to read data for the given [ChunkPosition],
     * if the chunk exists.
     */
    fun getInputStream(position: ChunkPosition): InputStream? =
            getIfPresent(position.toRegion())?.getInputStream(position)

    /**
     * Gets an output stream to write data for the given [ChunkPosition].
     */
    fun getOutputStream(position: ChunkPosition, format: AnvilChunkSectorFormat = AnvilChunkSectorFormat.Zlib): OutputStream =
            get(position.toRegion()).getOutputStream(position, format)

    companion object {

        private const val REGION_DIRECTORY_NAME = "region"
        private val REGION_FILE_PATTERN = "^r\\.([-]?[0-9]+)\\.([-]?[0-9]+)\\.${AnvilRegionFile.REGION_FILE_EXTENSION}$".toRegex()
    }
}

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
package org.lanternpowered.api.service.world

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.world.chunk.ChunkPosition

/**
 * A provider for chunk related data.
 */
interface ChunkStorage {

    /**
     * Gets whether a chunk exists at the given chunk coordinates.
     *
     * @param position The chunk position
     * @return Whether the chunk exists
     */
    fun exists(position: ChunkPosition): Boolean

    /**
     * Saves the data for the given chunk coordinates.
     *
     * @param position The chunk position
     * @param chunkData The chunk data to save
     */
    fun save(position: ChunkPosition, chunkData: DataView)

    /**
     * Deletes the data for the given chunk coordinates.
     *
     * @param position The chunk position
     * @return If the chunk was saved before
     */
    fun delete(position: ChunkPosition): Boolean

    /**
     * Loads the data for the given chunk coordinates, if it exists.
     *
     * @param position The chunk position
     * @return The chunk data
     */
    fun load(position: ChunkPosition): DataContainer?

    /**
     * Gets a sequence of all the chunk entries. Chunk data for each entry
     * should be loaded lazily when requested, so that not all the data is
     * loaded at the same time.
     */
    fun sequence(): Sequence<Entry>

    /**
     * Represents a chunk entry.
     */
    interface Entry {

        /**
         * The chunk position.
         */
        val position: ChunkPosition

        /**
         * Loads the chunk data, will return `null` if the
         * chunk data is no longer available.
         */
        fun load(): DataContainer?
    }
}

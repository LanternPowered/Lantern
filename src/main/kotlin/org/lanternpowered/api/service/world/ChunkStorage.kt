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
import java.util.concurrent.CompletableFuture

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
    fun exists(position: ChunkPosition): CompletableFuture<Boolean>

    /**
     * Saves the data for the given chunk coordinates.
     *
     * @param position The chunk position
     * @param chunkData The chunk data to save
     */
    fun save(position: ChunkPosition, chunkData: DataView): CompletableFuture<Unit>

    /**
     * Loads the data for the given chunk coordinates, if it exists.
     *
     * @param position The chunk position
     * @return The chunk data
     */
    fun load(position: ChunkPosition): CompletableFuture<DataContainer?>

    /**
     * Gets a sequence of all the chunk entries. This sequence is allowed
     * to be blocking on each entry. Chunk data for each entry should be
     * loaded lazily when requested, so that not all the data is loaded
     * at the same time.
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
         * Loads the chunk data, will fail exceptionally if the
         * chunk data is no longer available.
         */
        fun load(): CompletableFuture<DataContainer>
    }
}

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

import org.spongepowered.math.vector.Vector3i

/**
 * A provider for chunk related data.
 */
interface ChunkStorage {

    /**
     * Represents the size of a single chunk group. All the chunk groups provided
     * by this service will use this size.
     *
     * For the vanilla world format, this is (1, 16, 1).
     */
    val groupSize: Vector3i

    /**
     * Gets whether a chunk exists at the given chunk group coordinates.
     *
     * @param position The chunk position
     * @return Whether the chunk exists
     */
    fun exists(position: ChunkGroupPosition): Boolean

    /**
     * Saves the data for the given chunk group coordinates.
     *
     * @param position The chunk position
     * @param data The chunk group data to save
     */
    fun save(position: ChunkGroupPosition, data: ChunkGroupData)

    /**
     * Deletes the data for the given chunk group coordinates.
     *
     * @param position The chunk position
     * @return If the chunk was saved before
     */
    fun delete(position: ChunkGroupPosition): Boolean

    /**
     * Loads the data for the given chunk group coordinates, if it exists.
     *
     * @param position The chunk group position
     * @return The chunk group data
     */
    fun load(position: ChunkGroupPosition): ChunkGroupData?

    /**
     * Gets a sequence of all the chunk group entries. Chunk data for each entry
     * should be loaded lazily when requested, so that not all the data is
     * loaded at the same time.
     */
    fun sequence(): Sequence<Entry>

    /**
     * Represents a chunk entry.
     */
    interface Entry {

        /**
         * The chunk group position.
         */
        val position: ChunkGroupPosition

        /**
         * Loads the chunk group data, will return `null` if the
         * chunk data is no longer available.
         */
        fun load(): ChunkGroupData?
    }
}

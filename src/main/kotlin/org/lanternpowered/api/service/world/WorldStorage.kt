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
import org.lanternpowered.api.util.Identifiable
import org.lanternpowered.api.world.WorldProperties
import java.nio.file.Path
import java.util.UUID

/**
 * Represents a provider for world data.
 *
 * Scoreboard data will be located in `DataQuery.of("Scoreboard")`,
 * if it's present.
 */
interface WorldStorage : Identifiable {

    // TODO: Detach scoreboard data from the world, scoreboard data is
    //  per server, but is stored in the world in vanilla

    /**
     * The path of the directory.
     */
    val directory: Path

    /**
     * The name of the directory.
     */
    val directoryName: String

    /**
     * The chunk storage.
     */
    val chunks: ChunkStorage

    /**
     * Attempts to get a [Path] in the world storage to
     * store configuration files.
     */
    fun getConfigPath(name: String): Path

    /**
     * Acquires a lock to the world storage. While the
     * lock is active, worlds cannot be modified by
     * other sources.
     *
     * Returns the current one if there's already a
     * lock active.
     *
     * Returns `null` if the lock can't be acquired.
     */
    fun acquireLock(): Lock?

    /**
     * Deletes the world.
     */
    fun delete(): Boolean

    /**
     * Loads the world [DataContainer].
     */
    fun load(): DataContainer

    /**
     * Saves the world [DataView].
     *
     * This view will contain all the fields of [WorldProperties], and
     * may contain extra information of the world, like scoreboard data.
     *
     * @param data The world data
     */
    fun save(data: DataView)

    /**
     * Modifies the unique id of the world.
     *
     * @param uniqueId The unique id to set
     */
    fun uniqueId(uniqueId: UUID)

    /**
     * Represents a lock that holds a world storage.
     */
    interface Lock : AutoCloseable {

        /**
         * Releases the lock.
         */
        override fun close()
    }
}

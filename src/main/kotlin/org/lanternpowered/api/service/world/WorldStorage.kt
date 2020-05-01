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
import java.util.concurrent.CompletableFuture

/**
 * Represents a provider for world data.
 */
interface WorldStorage : Identifiable {

    /**
     * The name of the directory.
     */
    val directoryName: String

    /**
     * The chunk data provider.
     */
    val chunkStorage: ChunkStorage

    /**
     * Acquires a lock to the world data provider. While the
     * lock is active, worlds cannot be moved or deleted.
     *
     * This also prevents other server instances from
     * loading the world.
     *
     * Returns `null` if there's already another lock active.
     */
    fun acquireLock(): Lock?

    /**
     * Deletes the world.
     */
    fun delete(): CompletableFuture<Boolean>

    /**
     * Loads the world [DataContainer].
     */
    fun load(): CompletableFuture<DataContainer>

    /**
     * Saves the world [DataView].
     *
     * This view will contain all the fields of [WorldProperties], and
     * may contain extra information of the world, like scoreboard data.
     *
     * Scoreboard data will be located in `DataQuery.of("Scoreboard")`, if it's present.
     *
     * @param data The world data
     * @return The future that will be completed when the saving is done
     * @throws IllegalArgumentException If the unique id isn't the same as [getUniqueId]
     */
    fun save(data: DataView): CompletableFuture<Unit>

    /**
     * Represents a lock that holds a world data provider.
     */
    interface Lock {

        /**
         * Releases the lock.
         */
        fun release()
    }
}

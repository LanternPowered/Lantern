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

import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * A service that will handle all the loading and saving data related to worlds.
 */
interface WorldStorageService {

    /**
     * The directory path where all the data is located.
     */
    val directory: Path

    /**
     * A collection of all the providers of known worlds.
     */
    val all: Collection<WorldStorage>

    /**
     * Gets a data provider for the given world [UUID].
     *
     * @return The world data provider, or null if the world doesn't exist
     */
    operator fun get(uniqueId: UUID): WorldStorage?

    /**
     * Gets a data provider by the given world directory name.
     *
     * @param directoryName The directory name
     * @return The data provider, or null if the world doesn't exist
     */
    fun getByName(directoryName: String): WorldStorage?

    /**
     * Creates a new world for the given world directory name and [UUID]. The created
     * provider will be returned if it was successful.
     *
     * @param directoryName The directory name
     * @param uniqueId The unique id to use for the world
     */
    fun create(directoryName: String, uniqueId: UUID? = UUID.randomUUID()): CompletableFuture<WorldStorage?>

    /**
     * Attempts to copy the world at the source directory name to the copy directory name.
     *
     * @param sourceName The source directory name
     * @param copyName The copy or destination directory name
     * @return The provider of the new copy
     */
    fun copy(sourceName: String, copyName: String, uniqueId: UUID? = UUID.randomUUID()): CompletableFuture<WorldStorage?>

    /**
     * Attempts to copy the world at the source directory name to the copy directory name.
     *
     * @param oldName The old directory name
     * @param newName The new directory name
     * @return The provider of the new copy
     */
    fun move(oldName: String, newName: String): CompletableFuture<WorldStorage?>

}

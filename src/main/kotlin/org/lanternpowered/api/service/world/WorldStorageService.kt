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

/**
 * A service that will handle all the loading and saving of data related to worlds.
 */
interface WorldStorageService {

    /**
     * Is called when a new world storage is discovered.
     */
    val onDiscover: MutableCollection<(storage: WorldStorage) -> Unit>

    /**
     * Is called when a world storage is removed.
     */
    val onRemove: MutableCollection<(storage: WorldStorage) -> Unit>

    /**
     * The directory path where all the data is located.
     */
    val directory: Path

    /**
     * A collection of all the providers of known worlds.
     */
    val all: Collection<WorldStorage>

    /**
     * Attempts to get a [Path] in the storage service to
     * store configuration files.
     */
    fun getConfigPath(name: String): Path

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
    fun create(directoryName: String, uniqueId: UUID = UUID.randomUUID()): WorldStorage?

    /**
     * Attempts to copy the world at the source directory name to the copy directory name.
     *
     * @param sourceName The source directory name
     * @param copyName The copy or destination directory name
     * @return The provider of the new copy
     */
    fun copy(sourceName: String, copyName: String, uniqueId: UUID = UUID.randomUUID()): WorldStorage?

    /**
     * Attempts to copy the world at the source directory name to the copy directory name.
     *
     * @param oldName The old directory name
     * @param newName The new directory name
     * @return The provider of the new copy
     */
    fun move(oldName: String, newName: String): WorldStorage?
}

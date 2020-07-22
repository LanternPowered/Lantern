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

import org.lanternpowered.api.namespace.NamespacedKey
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
     * Gets a data provider by the given world key.
     *
     * @param key The key
     * @return The data provider, or null if the world doesn't exist
     */
    fun getByKey(key: NamespacedKey): WorldStorage?

    /**
     * Creates a new world for the given world key and [UUID]. The created
     * provider will be returned if it was successful.
     *
     * @param key The key
     * @param uniqueId The unique id to use for the world
     */
    fun create(key: NamespacedKey, uniqueId: UUID = UUID.randomUUID()): WorldStorage?

    /**
     * Attempts to copy the world at the source directory name to the copy directory name.
     *
     * @param sourceKey The source key
     * @param copyKey The copy or destination key
     * @return The provider of the new copy
     */
    fun copy(sourceKey: NamespacedKey, copyKey: NamespacedKey, uniqueId: UUID = UUID.randomUUID()): WorldStorage?

    /**
     * Attempts to copy the world at the source directory name to the copy directory name.
     *
     * @param oldKey The old key
     * @param newKey The new key
     * @return The provider of the new copy
     */
    fun move(oldKey: NamespacedKey, newKey: NamespacedKey): WorldStorage?
}

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
package org.lanternpowered.server.service.world

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.service.world.WorldStorageService
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.game.Lantern
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * A world data service that will handle world data similar like a vanilla
 * minecraft server. A vanilla world should be able to be pasted in and be
 * completely compatible, if the world data is upgraded to the supported
 * version.
 *
 * The directory structure will be the following for example:
 *  /worlds
 *     /config.json        -- The global configuration related to worlds
 *     /minecraft          -- A directory containing all the default worlds created by "minecraft"
 *        /overworld
 *           /config.json  -- The configuration of a specific world
 *           /data         -- The world data, compatible with sponge and vanilla worlds
 *              /region
 *              /level.dat
 *              /sponge_level.dat
 *        /the_end
 *        /the_nether
 *     /myplugin           -- A directory containing all the worlds created by "myplugin"
 *        /myworld
 *
 * @property directory The directory where all the worlds will be stored
 */
class DefaultWorldStorageService(
        override val directory: Path
) : WorldStorageService, Closeable {

    private val knownStoragesByUniqueId = ConcurrentHashMap<UUID, LanternWorldStorage>()
    private val knownStoragesByKey = ConcurrentHashMap<ResourceKey, LanternWorldStorage>()

    private val modifyLock = Any()

    override val onDiscover: MutableList<(storage: WorldStorage) -> Unit> = Collections.synchronizedList(mutableListOf())
    override val onRemove: MutableCollection<(storage: WorldStorage) -> Unit> = Collections.synchronizedList(mutableListOf())

    /**
     * Imports the world directory.
     */
    fun import(directory: Path) {
        // First check all the sub directories
        for (subDirectory in Files.walk(directory, 1)) {
            if (LanternWorldStorage.isWorld(subDirectory))
                importWorld(subDirectory)
        }
        // Import the root world
        if (LanternWorldStorage.isWorld(this.directory))
            importWorld(directory, "DIM0")
    }

    private fun importWorld(path: Path, oldName: String = path.fileName.toString()): Path {
        val newName = when (oldName) {
            "DIM-1" -> "the_nether"
            "DIM1" -> "the_end"
            "DIM0" -> "overworld"
            else -> oldName
        }
        var destination = this.directory.resolve(newName)
        if (Files.exists(destination)) {
            var number = 1
            // Start numbering if the directory is already in use
            while (Files.exists(destination))
                destination = this.directory.resolve("$newName${++number}")
            Lantern.getLogger().info("The directory $newName was already in use, so the imported world " +
                    "from $oldName will be moved to $destination.")
        }
        com.google.common.io.Files.move(path.toFile(), destination.resolve(LanternWorldStorage.DATA_DIRECTORY_NAME).toFile())
        // Remove unneeded data
        LanternWorldStorage.cleanupWorld(destination)
        return destination
    }

    override val all: Collection<WorldStorage>
        get() = this.knownStoragesByUniqueId.values.toImmutableList()

    override fun getConfigPath(name: String): Path =
            this.directory.resolve(name)

    override fun get(uniqueId: UUID): WorldStorage? =
            this.knownStoragesByUniqueId[uniqueId]

    override fun getByKey(key: ResourceKey): WorldStorage? =
            this.knownStoragesByKey[key]

    override fun create(key: ResourceKey, uniqueId: UUID): WorldStorage? {
        synchronized(this.modifyLock) {
            if (this.knownStoragesByKey.containsKey(key) ||
                    this.knownStoragesByUniqueId.containsKey(uniqueId))
                return null

            val worldDirectory = this.directory.resolve(key.namespace).resolve(key.value)
            if (Files.exists(worldDirectory) && Files.list(worldDirectory).count() > 0)
                return null

            Files.createDirectories(worldDirectory)
            val worldStorage = LanternWorldStorage(key, uniqueId, worldDirectory)

            this.knownStoragesByKey[key] = worldStorage
            this.knownStoragesByUniqueId[uniqueId] = worldStorage

            return worldStorage
        }
    }

    override fun copy(sourceKey: ResourceKey, copyKey: ResourceKey, uniqueId: UUID): WorldStorage? {
        TODO("Not yet implemented")
    }

    override fun move(oldKey: ResourceKey, newKey: ResourceKey): WorldStorage? {
        TODO("Not yet implemented")
    }

    override fun close() {
        synchronized(this.modifyLock) {
            for (storage in this.knownStoragesByKey.values)
                storage.close()
            this.knownStoragesByKey.clear()
            this.knownStoragesByUniqueId.clear()
        }
    }
}

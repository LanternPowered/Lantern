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
package org.lanternpowered.server.world

import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.service.world.WorldStorageService
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.WorldArchetype
import org.lanternpowered.api.world.WorldManager
import org.lanternpowered.api.world.WorldProperties
import org.lanternpowered.server.util.ThreadHelper
import java.nio.file.Path
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier
import kotlin.concurrent.withLock
import kotlin.math.max

val World.regionManager: WorldRegionManager get() = TODO()

class LanternWorldManager(
        val ioExecutor: ExecutorService,
        val worldStorageService: WorldStorageService
) : WorldManager {

    // The executor that will be used for world related tasks
    private val worldExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
            1, 1, 60L, TimeUnit.MINUTES, SynchronousQueue<Runnable>(), ThreadHelper.newThreadFactory())

    private class WorldEntry(
            val storage: WorldStorage,
            val properties: LanternWorldProperties
    ) {
        val modifyLock = ReentrantLock()

        // The current instance of the world, if the world is loaded
        @Volatile var world: World? = null
    }

    private val registrations = linkedMapOf<String, WorldArchetype>()

    private val entryByDirectory = ConcurrentHashMap<String, WorldEntry>()
    private val entryByUniqueId = ConcurrentHashMap<UUID, WorldEntry>()
    private val modifyLock = Any()

    init {
        this.worldStorageService.onDiscover += this::onDiscover
        this.worldStorageService.onRemove += this::onRemove
    }

    override fun getSavesDirectory(): Path = this.worldStorageService.directory
    override fun getDefaultPropertiesName(): String = this.registrations.entries.firstOrNull()?.key ?: "unknown"
    override fun getDefaultProperties(): Optional<WorldProperties> = this.entryByDirectory[this.defaultPropertiesName]?.properties.optional()

    private fun onDiscover(storage: WorldStorage) {
        // Just load the new entry
        loadEntry(storage)
    }

    private fun onRemove(storage: WorldStorage) {
        // The world directory was removed, this shouldn't
        // happen to loaded worlds
        synchronized(this.modifyLock) {
            val entry = this.entryByDirectory.remove(storage.directoryName) ?: return
            this.entryByUniqueId.remove(storage.uniqueId)
            if (entry.world != null) {
                // The world was currently loaded?
                throw IllegalStateException("A loaded world got removed: ${storage.directoryName}")
            }
        }
    }

    override fun submitRegistration(directoryName: String, archetype: WorldArchetype): Boolean {
        // TODO: Move to event
        @Suppress("NAME_SHADOWING")
        val directoryName = directoryName.toLowerCase()
        if (this.registrations.containsKey(directoryName))
            return false
        this.registrations[directoryName] = archetype
        return true
    }

    override fun copyWorld(directoryName: String, copyName: String): CompletableFuture<Optional<WorldProperties>> =
            CompletableFuture.supplyAsync(Supplier { copyWorld0(directoryName, copyName) }, this.ioExecutor)

    private fun copyWorld0(directoryName: String, copyName: String): Optional<WorldProperties> {
        // The copy directory is already in use
        if (this.entryByDirectory.contains(copyName))
            return emptyOptional()
        val sourceEntry = this.entryByDirectory[directoryName]
                ?: return emptyOptional() // The target world doesn't exist
        val newStorage = this.worldStorageService.copy(directoryName, copyName)
                ?: return emptyOptional() // Copying the world failed for some reason
        // TODO: Disable saving for the source world, if it's loaded
        val newEntry = loadEntry(newStorage)
        return newEntry.properties.optional()
    }

    override fun renameWorld(oldDirectoryName: String, newDirectoryName: String): CompletableFuture<Optional<WorldProperties>> =
            CompletableFuture.supplyAsync(Supplier { renameWorld0(oldDirectoryName, newDirectoryName) }, this.ioExecutor)

    private fun renameWorld0(oldDirectoryName: String, newDirectoryName: String): Optional<WorldProperties> {
        val entry = this.entryByDirectory[oldDirectoryName] ?: return emptyOptional()
        entry.modifyLock.withLock {
            // The world is currently loaded
            if (entry.world != null) {
                // The world first needs to be unloaded, this can fail
                // if there are still players in the world
                if (!unloadWorld(entry))
                    return emptyOptional()
            }
            val newStorage = this.worldStorageService.move(oldDirectoryName, newDirectoryName)
                    ?: return emptyOptional() // Moving failed
            val newEntry = loadEntry(newStorage)
            return newEntry.properties.optional()
        }
    }

    private fun loadEntry(storage: WorldStorage): WorldEntry {
        val data = storage.load()
        val properties = WorldPropertiesSerializer.deserialize(storage.directoryName, data)
        val entry = WorldEntry(storage, properties)
        synchronized(this.modifyLock) {
            var previous = this.entryByDirectory.putIfAbsent(storage.directoryName, entry)
            // The entry was already loaded
            if (previous != null)
                return previous
            previous = this.entryByUniqueId.putIfAbsent(storage.uniqueId, entry)
            // There already exists a world with the unique id,
            // we'll need to generate a new one
            if (previous != null) {
                val uniqueId = UUID.randomUUID()
                storage.uniqueId(uniqueId)
                // Try to add it again
                check(this.entryByUniqueId.putIfAbsent(uniqueId, entry) == null)
            }
        }
        return entry
    }

    override fun deleteWorld(directoryName: String): CompletableFuture<Boolean> =
            CompletableFuture.supplyAsync(Supplier { deleteWorld0(directoryName) }, this.ioExecutor)

    private fun deleteWorld0(directoryName: String): Boolean {
        val entry = this.entryByDirectory[directoryName]
                ?: return false
        entry.modifyLock.withLock {
            // The world is currently loaded, so it can't be removed
            if (entry.world != null)
                return false
            return entry.storage.delete()
        }
    }

    override fun loadWorld(directoryName: String): CompletableFuture<Optional<World>> {
        TODO("Not yet implemented")
    }

    override fun loadWorld(properties: WorldProperties): CompletableFuture<Optional<World>> {
        TODO("Not yet implemented")
    }

    private fun unloadWorld(entry: WorldEntry): Boolean {
        TODO("Not yet implemented")
    }

    override fun unloadWorld(world: World): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun createProperties(directoryName: String, archetype: WorldArchetype): CompletableFuture<Optional<WorldProperties>> =
            CompletableFuture.supplyAsync(Supplier { createProperties0(directoryName, archetype) }, this.ioExecutor)

    private fun createProperties0(directoryName: String, archetype: WorldArchetype): Optional<WorldProperties> {
        if (this.entryByDirectory.containsKey(directoryName))
            return emptyOptional()
        val storage = this.worldStorageService.create(directoryName)
                ?: return emptyOptional() // The construction failed
        val properties = LanternWorldProperties(directoryName)
        val entry = WorldEntry(storage, properties)
        entry.modifyLock.withLock {
            val previous = this.entryByDirectory.putIfAbsent(storage.directoryName, entry)
            // Someone beat us to it
            if (previous != null)
                return emptyOptional()
            this.entryByUniqueId.put(storage.uniqueId, entry)
        }
        entry.modifyLock.withLock {
            // TODO: Write config file
            // Copy all the information from the archetype to the properties
            properties.load(archetype)
            // Write the world data
            storage.save(WorldPropertiesSerializer.serialize(properties))
        }
        return properties.optional()
    }

    override fun saveProperties(properties: WorldProperties): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllProperties(): Collection<WorldProperties> =
            this.entryByDirectory.values.asSequence()
                    .map { it.properties }
                    .toImmutableList()

    override fun getUnloadedProperties(): Collection<WorldProperties> =
            this.entryByDirectory.values.asSequence()
                    .filter { it.world == null }
                    .map { it.properties }
                    .toImmutableList()

    override fun getProperties(directoryName: String): Optional<WorldProperties> =
            this.entryByDirectory[directoryName.toLowerCase()]?.properties.optional()

    override fun getProperties(uniqueId: UUID): Optional<WorldProperties> =
            this.entryByUniqueId[uniqueId]?.properties.optional()

    override fun getWorlds(): Collection<World> =
            this.entryByDirectory.values.asSequence()
                    .map { it.world }
                    .filterNotNull()
                    .toImmutableList()

    override fun getWorld(directoryName: String): Optional<World> =
            this.entryByDirectory[directoryName.toLowerCase()]?.world.optional()

    override fun getWorld(uniqueId: UUID): Optional<World> =
            this.entryByUniqueId[uniqueId]?.world.optional()

    /**
     * Runs all the world updates.
     */
    fun update() {
        val entries = this.entryByDirectory.values.toList()
                .filter { it.world != null }
        val locked = mutableListOf<WorldEntry>()
        try {
            for (entry in entries) {
                if (!entry.modifyLock.tryLock()) {
                    // The world is being modified, lets skip it for now, could
                    // be getting deleted
                    continue
                }
                // The world was unloaded in the meantime
                if (entry.world == null) {
                    entry.modifyLock.unlock()
                    continue
                }
                locked += entry
            }

            // Update the amount of threads that will be used
            // TODO: Make this configurable? Figure out what works best
            val poolSize = 2 + 2 * max(0, locked.size - 1)
            this.worldExecutor.maximumPoolSize = poolSize
            this.worldExecutor.corePoolSize = poolSize

            // Start collecting tasks
            val tasks = mutableListOf<() -> Unit>()
            for (entry in locked) {
                val regionManager = entry.world!!.regionManager
                regionManager.update { tasks.add(it) }
            }

            // Randomize the tasks, to prevent that the world order
            // determines which world gets executed first.
            tasks.shuffle()

            // Queue all the tasks and wait for them
            val latch = CountDownLatch(tasks.size)
            for (task in tasks) {
                this.worldExecutor.execute {
                    try {
                        task()
                    } finally {
                        latch.countDown()
                    }
                }
            }
            latch.await()
        } finally {
            for (entry in locked)
                entry.modifyLock.unlock()
        }
    }
}

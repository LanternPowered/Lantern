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

import org.lanternpowered.api.Game
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.event.lifecycle.RegisterWorldEvent
import org.lanternpowered.api.key.namespacedKey
import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.service.world.WorldStorageService
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.WorldArchetype
import org.lanternpowered.api.world.WorldManager
import org.lanternpowered.api.world.WorldProperties
import org.lanternpowered.server.LanternServerNew
import org.lanternpowered.server.util.SyncLanternThread
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier
import kotlin.concurrent.withLock
import kotlin.math.max

class LanternWorldManager(
        val server: LanternServerNew,
        val ioExecutor: ExecutorService,
        val worldStorageService: WorldStorageService
) : WorldManager {

    // The executor that will be used for world related tasks
    private val worldExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
            1, 1, 60L, TimeUnit.MINUTES, SynchronousQueue<Runnable>(), ThreadFactory { SyncLanternThread(it) })

    private class WorldEntry(
            val storage: WorldStorage,
            val properties: LanternWorldProperties
    ) {
        val modifyLock = ReentrantLock()

        /**
         * A lock which makes sure a world storage can't be modified.
         */
        var storageLock: WorldStorage.Lock? = null

        /**
         * The current instance of the world, if the world is loaded
         */
        @Volatile var world: LanternWorldNew? = null
    }

    private val entryByKey = ConcurrentHashMap<NamespacedKey, WorldEntry>()
    private val entryByUniqueId = ConcurrentHashMap<UUID, WorldEntry>()
    private val modifyLock = Any()

    fun init() {
        for (storage in this.worldStorageService.all)
            loadEntry(storage)

        this.worldStorageService.onDiscover += this::onDiscover
        this.worldStorageService.onRemove += this::onRemove

        val registrations = registerWorlds()
        for ((key, archetype) in registrations)
            createPropertiesNow(key, archetype)
    }

    override fun getDefaultPropertiesKey(): NamespacedKey =
            this.entryByKey.entries.firstOrNull()?.key ?: namespacedKey("lantern", "unknown")

    override fun getDefaultProperties(): Optional<WorldProperties> =
            this.entryByKey[this.defaultPropertiesKey]?.properties.optional()

    private fun onDiscover(storage: WorldStorage) {
        // Just load the new entry
        loadEntry(storage)
    }

    private fun onRemove(storage: WorldStorage) {
        // The world directory was removed, this shouldn't
        // happen to loaded worlds
        synchronized(this.modifyLock) {
            val entry = this.entryByKey.remove(storage.key) ?: return
            this.entryByUniqueId.remove(storage.uniqueId)
            if (entry.world != null) {
                // The world was currently loaded?
                throw IllegalStateException("A loaded world got removed: ${storage.key}")
            }
        }
    }

    private fun registerWorlds(): Map<NamespacedKey, WorldArchetype> {
        val registrations = mutableMapOf<NamespacedKey, WorldArchetype>()
        val cause = CauseStackManager.currentCause
        val event = object : RegisterWorldEvent {
            override fun getCause(): Cause = cause
            override fun getGame(): Game = server.game
            override fun register(key: NamespacedKey, archetype: WorldArchetype) {
                registrations.putIfAbsent(key, archetype)
            }
        }
        this.server.game.eventManager.post(event)
        return registrations
    }

    override fun copyWorld(sourceKey: NamespacedKey, copyValue: String): CompletableFuture<Optional<WorldProperties>> =
            CompletableFuture.supplyAsync(Supplier { copyWorld0(sourceKey, copyValue) }, this.ioExecutor)

    private fun copyWorld0(sourceKey: NamespacedKey, copyValue: String): Optional<WorldProperties> {
        val copyKey = namespacedKey(sourceKey.namespace, copyValue)
        // The copy directory is already in use
        if (this.entryByKey.contains(copyKey))
            return emptyOptional()
        val sourceEntry = this.entryByKey[sourceKey]
                ?: return emptyOptional() // The target world doesn't exist
        val newStorage = this.worldStorageService.copy(sourceKey, copyKey)
                ?: return emptyOptional() // Copying the world failed for some reason
        // TODO: Disable saving for the source world, if it's loaded
        val newEntry = loadEntry(newStorage)
        return newEntry.properties.optional()
    }

    override fun renameWorld(oldKey: NamespacedKey, newValue: String): CompletableFuture<Optional<WorldProperties>> =
            CompletableFuture.supplyAsync(Supplier { renameWorld0(oldKey, newValue) }, this.ioExecutor)

    private fun renameWorld0(oldKey: NamespacedKey, newValue: String): Optional<WorldProperties> {
        val newKey = namespacedKey(oldKey.namespace, newValue)
        val entry = this.entryByKey[oldKey] ?: return emptyOptional()
        entry.modifyLock.withLock {
            // The world is currently loaded
            if (entry.world != null) {
                // The world first needs to be unloaded, this can fail
                // if there are still players in the world
                if (!unloadWorld(entry))
                    return emptyOptional()
            }
            val newStorage = this.worldStorageService.move(oldKey, newKey)
                    ?: return emptyOptional() // Moving failed
            val newEntry = loadEntry(newStorage)
            return newEntry.properties.optional()
        }
    }

    private fun loadEntry(storage: WorldStorage): WorldEntry {
        val data = storage.load()
        val properties = WorldPropertiesSerializer.deserialize(storage.key, data)
        val entry = WorldEntry(storage, properties)
        synchronized(this.modifyLock) {
            var previous = this.entryByKey.putIfAbsent(storage.key, entry)
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

    override fun deleteWorld(key: NamespacedKey): CompletableFuture<Boolean> =
            CompletableFuture.supplyAsync(Supplier { deleteWorld0(key) }, this.ioExecutor)

    private fun deleteWorld0(key: NamespacedKey): Boolean {
        val entry = this.entryByKey[key]
                ?: return false
        entry.modifyLock.withLock {
            // The world is currently loaded, so it can't be removed
            if (entry.world != null)
                return false
            return entry.storage.delete()
        }
    }

    override fun loadWorld(key: NamespacedKey): CompletableFuture<Optional<World>> =
            CompletableFuture.supplyAsync(Supplier { loadWorld0(key).optional() }, this.ioExecutor)

    private fun loadWorld0(key: NamespacedKey): World? {
        val entry = this.entryByKey[key]
                ?: return null
        return loadWorld(entry)
    }

    override fun loadWorld(properties: WorldProperties): CompletableFuture<Optional<World>> =
            CompletableFuture.supplyAsync(Supplier { loadWorld0(properties).optional() }, this.ioExecutor)

    private fun loadWorld0(properties: WorldProperties): World? {
        val entry = this.entryByUniqueId[properties.uniqueId]
                ?: return null
        return loadWorld(entry)
    }

    private fun loadWorld(entry: WorldEntry): World? {
        entry.modifyLock.withLock {
            // The world is already loaded
            if (entry.world != null)
                return entry.world
            val storageLock = entry.storage.acquireLock()
                    ?: return null
            entry.storageLock = storageLock
            val world = LanternWorldNew(this.server, entry.properties, entry.storage, this.ioExecutor)
            entry.world = world
            entry.properties.setWorld(world)
            return world
        }
    }

    override fun unloadWorld(world: World): CompletableFuture<Boolean> =
            CompletableFuture.supplyAsync(Supplier { unloadWorld0(world) }, this.ioExecutor)

    private fun unloadWorld0(world: World): Boolean {
        val entry = this.entryByUniqueId[world.uniqueId]
                ?: return false
        return unloadWorld(entry)
    }

    private fun unloadWorld(entry: WorldEntry): Boolean {
        entry.modifyLock.withLock {
            val world = entry.world
                    ?: return false // The world isn't loaded
            world.unload()
            entry.storage.save(WorldPropertiesSerializer.serialize(entry.properties))
            entry.properties.setWorld(null)
            entry.storageLock?.close()
            entry.storageLock = null
            entry.world = null
            return true
        }
    }

    override fun createProperties(key: NamespacedKey, archetype: WorldArchetype): CompletableFuture<Optional<WorldProperties>> =
            CompletableFuture.supplyAsync(Supplier { createPropertiesNow(key, archetype) }, this.ioExecutor)

    private fun createPropertiesNow(key: NamespacedKey, archetype: WorldArchetype): Optional<WorldProperties> {
        if (this.entryByKey.containsKey(key))
            return emptyOptional()
        val storage = this.worldStorageService.create(key)
                ?: return emptyOptional() // The construction failed
        val properties = LanternWorldProperties(key, UUID.randomUUID())
        val entry = WorldEntry(storage, properties)
        entry.modifyLock.withLock {
            val previous = this.entryByKey.putIfAbsent(storage.key, entry)
            // Someone beat us to it
            if (previous != null)
                return emptyOptional()
            this.entryByUniqueId[storage.uniqueId] = entry
            // TODO: Write config file
            // Copy all the information from the archetype to the properties
            properties.loadFrom(archetype)
            // Write the world data
            storage.save(WorldPropertiesSerializer.serialize(properties))
        }
        return properties.optional()
    }

    override fun saveProperties(properties: WorldProperties): CompletableFuture<Boolean> =
            CompletableFuture.supplyAsync(Supplier { saveProperties0(properties) }, this.ioExecutor)

    private fun saveProperties0(properties: WorldProperties): Boolean {
        val entry = this.entryByUniqueId[properties.uniqueId]
                ?: return false
        entry.modifyLock.withLock {
            check(properties === entry.properties)
            entry.storage.save(WorldPropertiesSerializer.serialize(properties as LanternWorldProperties))
            return true
        }
    }

    override fun getAllProperties(): Collection<WorldProperties> =
            this.entryByKey.values.asSequence()
                    .map { it.properties }
                    .toImmutableList()

    override fun getUnloadedProperties(): Collection<WorldProperties> =
            this.entryByKey.values.asSequence()
                    .filter { it.world == null }
                    .map { it.properties }
                    .toImmutableList()

    override fun getProperties(key: NamespacedKey): Optional<WorldProperties> =
            this.entryByKey[key]?.properties.optional()

    fun getProperties(uniqueId: UUID): Optional<WorldProperties> =
            this.entryByUniqueId[uniqueId]?.properties.optional()

    override fun getWorlds(): Collection<World> =
            this.entryByKey.values.asSequence()
                    .map { it.world }
                    .filterNotNull()
                    .toImmutableList()

    override fun getWorld(key: NamespacedKey): Optional<World> =
            this.entryByKey[key]?.world.optional()

    fun getWorld(uniqueId: UUID): Optional<World> =
            this.entryByUniqueId[uniqueId]?.world.optional()

    /**
     * Runs all the world updates.
     */
    fun update() {
        val entries = this.entryByKey.values.toList()
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
                val world = entry.world!!
                val regionManager = world.regionManager
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

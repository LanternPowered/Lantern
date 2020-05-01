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

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.data.persistence.getOrCreateView
import org.lanternpowered.api.service.world.ChunkStorage
import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils
import org.lanternpowered.server.service.world.anvil.AnvilChunkStorage
import org.lanternpowered.server.util.SafeIO
import java.io.DataInputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.channels.OverlappingFileLockException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

class DefaultWorldDataProvider(
        private val uniqueId: UUID,
        private val directory: Path,
        private val executorService: ExecutorService
) : WorldStorage {

    private var providerLock: Lock? = null
    private val lock = Any()

    override fun getUniqueId(): UUID = this.uniqueId

    override val directoryName: String
        get() = this.directory.fileName.toString()

    override val chunkStorage: ChunkStorage = AnvilChunkStorage(this.executorService, this.directory)

    override fun acquireLock(): WorldStorage.Lock? {
        synchronized(this.lock) {
            var providerLock = this.providerLock
            if (providerLock != null)
                return null
            providerLock = Lock()
            if (!providerLock.acquire())
                return null
            this.providerLock = null
            return providerLock
        }
    }

    inner class Lock : WorldStorage.Lock {

        private lateinit var fileChannel: FileChannel
        private lateinit var fileLock: FileLock

        private var released: Boolean = false

        fun acquire(): Boolean {
            val file = directory.resolve(SESSION_LOCK_FILE)

            val channel = RandomAccessFile(file.toFile(), "rw").channel
            val lock = try {
                this.fileChannel.tryLock()
            } catch (ex: OverlappingFileLockException) {
                null
            } ?: return false

            var release = false
            try {
                val timestamp = ByteBuffer.allocate(Long.SIZE_BYTES)
                timestamp.putLong(Instant.now().toEpochMilli())

                channel.position(0)
                channel.write(timestamp)
                channel.force(true)

                this.fileChannel = channel
                this.fileLock = lock
            } catch (ex: Exception) {
                release = true
                throw ex
            } finally {
                if (release) {
                    lock.release()
                    channel.close()
                }
            }
            return true
        }

        override fun release() {
            synchronized(lock) {
                if (this.released)
                    throw IllegalStateException("The lock is already released.")
                this.released = true
                this.fileLock.release()
                this.fileChannel.close()
            }
        }
    }

    override fun delete(): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync(Supplier {
            if (!Files.exists(this.directory))
                return@Supplier false
            Files.walkFileTree(this.directory, object : SimpleFileVisitor<Path>() {
                override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                    Files.delete(path)
                    return FileVisitResult.CONTINUE
                }
                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }
            })
            true
        }, this.executorService)
    }

    override fun load(): CompletableFuture<DataContainer> =
            CompletableFuture.supplyAsync(Supplier { loadBlocking() }, this.executorService)

    private fun loadBlocking(): DataContainer {
        val data = SafeIO.read(this.directory.resolve(LEVEL_DATA_FILE)) { path ->
            NbtStreamUtils.read(Files.newInputStream(path), true)
        }
        check(data != null) { "The level.dat file of the world is missing." }

        val spongeLevelData = loadSpongeData(this.directory)
        if (spongeLevelData != null)
            data.getOrCreateView(LEVEL_DATA).set(SPONGE_DATA, spongeLevelData)

        val scoreboardData = SafeIO.read(this.directory.resolve(SCOREBOARD_DATA_FILE)) { path ->
            NbtStreamUtils.read(Files.newInputStream(path), true)
        }?.getView(DATA)?.orNull()

        if (scoreboardData != null)
            data.set(SCOREBOARD, scoreboardData)

        return data
    }

    override fun save(data: DataView): CompletableFuture<Unit> =
            CompletableFuture.supplyAsync(Supplier { saveBlocking(data) }, this.executorService)

    private fun saveBlocking(data: DataView) {
        // In vanilla minecraft, some pieces of world data are in multiple files
        val scoreboardView = data.getView(SCOREBOARD).orNull()
        if (scoreboardView != null) {
            val rootScoreboardView = DataContainer.createNew()
                    .set(DATA, scoreboardView)
            data.remove(SCOREBOARD)
            SafeIO.write(this.directory.resolve(SCOREBOARD_DATA_FILE)) { path ->
                NbtStreamUtils.write(rootScoreboardView, Files.newOutputStream(path), true)
            }
        }

        val levelData = data.getView(LEVEL_DATA).orNull()

        val spongeData: DataView? = if (levelData != null) {
            val spongeData = levelData.getView(SPONGE_DATA).orNull()
            if (spongeData != null) {
                levelData.remove(SPONGE_DATA)
            }
            spongeData
        } else null

        val spongeDataFile = this.directory.resolve(SPONGE_LEVEL_DATA_FILE)
        if (spongeData != null) {
            val rootSpongeLevelData = DataContainer.createNew()
                    .set(SPONGE_DATA, spongeData)
            SafeIO.write(spongeDataFile) { path ->
                NbtStreamUtils.write(rootSpongeLevelData, Files.newOutputStream(path), true)
            }
        } else {
            Files.deleteIfExists(spongeDataFile)
        }

        SafeIO.write(this.directory.resolve(LEVEL_DATA_FILE)) { path ->
            NbtStreamUtils.write(data, Files.newOutputStream(path), true)
        }
    }

    companion object {

        private fun loadSpongeData(directory: Path): DataView? {
            return SafeIO.read(directory.resolve(SPONGE_LEVEL_DATA_FILE)) { path ->
                NbtStreamUtils.read(Files.newInputStream(path), true)
            }?.getView(SPONGE_DATA)?.orNull()
        }

        /**
         * Attempts to load a [UUID] from the given world directory,
         * this returns an unique id if found and a task to cleanup
         * old data.
         */
        fun loadUniqueId(directory: Path): Pair<UUID, () -> Unit>? {
            val spongeData = loadSpongeData(directory)
            if (spongeData != null) {
                val most = spongeData.getLong(UUID_MOST).orNull()
                val least = spongeData.getLong(UUID_LEAST).orNull()
                if (most != null && least != null)
                    return UUID(most, least) to {}
            }
            val bukkitFile = directory.resolve(BUKKIT_UUID_DATA_FILE)
            if (Files.exists(bukkitFile)) {
                val uuid = DataInputStream(Files.newInputStream(bukkitFile)).use { input ->
                    val most = input.readLong()
                    val least = input.readLong()
                    UUID(most, least)
                }
                return uuid to {
                    Files.deleteIfExists(bukkitFile)
                    Unit
                }
            }
            return null
        }

        private const val SCOREBOARD_DATA_FILE = "scoreboard.dat"
        private const val LEVEL_DATA_FILE = "level.dat"
        private const val SPONGE_LEVEL_DATA_FILE = "level_sponge.dat"
        private const val BUKKIT_UUID_DATA_FILE = "uid.dat"
        private const val SESSION_LOCK_FILE = "session.lock"

        private val UUID_MOST = DataQuery.of("UUIDMost")
        private val UUID_LEAST = DataQuery.of("UUIDLeast")
        private val LEVEL_DATA = DataQuery.of("Level")
        private val SPONGE_DATA = DataQuery.of("SpongeData")
        private val SCOREBOARD = DataQuery.of("Scoreboard")
        private val DATA = DataQuery.of("data")
    }
}

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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.data.persistence.getOrCreateView
import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils
import org.lanternpowered.server.service.user.LanternUserStorage
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
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class LanternWorldStorage(
        override val key: NamespacedKey,
        @Volatile private var uniqueId: UUID,
        override val directory: Path
) : WorldStorage {

    private var sessionLock: Lock? = null
    private val lock = ReentrantReadWriteLock()

    override fun getUniqueId(): UUID = this.uniqueId

    private val dataDirectory: Path = this.directory.resolve(DATA_DIRECTORY_NAME)

    override val chunks: AnvilChunkStorage = AnvilChunkStorage(this.dataDirectory.resolve(REGION_DIRECTORY_NAME))

    override fun getConfigPath(name: String): Path = this.directory.resolve(name)

    fun close() {
        this.chunks.close()
    }

    override fun acquireLock(): WorldStorage.Lock? {
        this.lock.write {
            var sessionLock = this.sessionLock
            if (sessionLock != null)
                return sessionLock
            sessionLock = Lock()
            if (!sessionLock.acquire())
                return null
            this.sessionLock = null
            return sessionLock
        }
    }

    inner class Lock : WorldStorage.Lock {

        private lateinit var fileChannel: FileChannel
        private lateinit var fileLock: FileLock

        private var released: Boolean = false

        fun acquire(): Boolean {
            val file = dataDirectory.resolve(SESSION_LOCK_FILE)

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
            return !release
        }

        override fun close() {
            lock.write {
                if (this.released)
                    throw IllegalStateException("The lock is already released.")
                this.released = true
                this.fileLock.release()
                this.fileChannel.close()
            }
        }
    }

    override fun uniqueId(uniqueId: UUID) {
        this.lock.write { uniqueId0(uniqueId) }
    }

    private fun uniqueId0(uniqueId: UUID) {
        val spongeData = loadSpongeData(this.dataDirectory) ?: DataContainer.createNew()
        spongeData.addUniqueId(uniqueId)
        saveSpongeData(this.dataDirectory, spongeData)
        this.uniqueId = uniqueId
    }

    override fun delete(): Boolean {
        if (!Files.exists(this.dataDirectory))
            return false
        this.lock.write { delete0() }
        return true
    }

    private fun delete0() {
        Files.walkFileTree(this.dataDirectory, object : SimpleFileVisitor<Path>() {
            override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(path)
                return FileVisitResult.CONTINUE
            }

            override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                Files.delete(dir)
                return FileVisitResult.CONTINUE
            }
        })
    }

    override fun load(): DataContainer = this.lock.read { load0() }

    private fun load0(): DataContainer {
        val data = readSafely(this.dataDirectory.resolve(LEVEL_DATA_FILE)) { path ->
            NbtStreamUtils.read(Files.newInputStream(path), true)
        }
        check(data != null) { "The level.dat file of the world is missing." }

        val spongeData = loadSpongeData(this.dataDirectory)
        if (spongeData != null) {
            spongeData.remove(UUID_MOST)
            spongeData.remove(UUID_LEAST)
            data.getOrCreateView(LEVEL_DATA).set(SPONGE_DATA, spongeData)
        }

        val scoreboardData = readSafely(this.dataDirectory.resolve(SCOREBOARD_DATA_FILE)) { path ->
            NbtStreamUtils.read(Files.newInputStream(path), true)
        }?.getView(DATA)?.orNull()

        if (scoreboardData != null)
            data.set(SCOREBOARD, scoreboardData)

        return data
    }

    override fun save(data: DataView) {
        this.lock.write { save0(data) }
    }

    private fun save0(data: DataView) {
        // In vanilla minecraft, some pieces of world data are in multiple files
        val scoreboardData = data.getView(SCOREBOARD).orNull()
        if (scoreboardData != null) {
            val rootScoreboardData = DataContainer.createNew()
                    .set(DATA, scoreboardData)
            data.remove(SCOREBOARD)
            writeSafely(this.dataDirectory.resolve(SCOREBOARD_DATA_FILE)) { path ->
                NbtStreamUtils.write(rootScoreboardData, Files.newOutputStream(path), true)
            }
        }

        val levelData = data.getView(LEVEL_DATA).orNull()

        val spongeData: DataView = if (levelData != null) {
            val spongeData = levelData.getView(SPONGE_DATA).orNull()
            if (spongeData != null)
                levelData.remove(SPONGE_DATA)
            spongeData?.copy() ?: DataContainer.createNew()
        } else DataContainer.createNew()
        spongeData.addUniqueId(this.uniqueId)
        saveSpongeData(this.dataDirectory, spongeData)

        writeSafely(this.dataDirectory.resolve(LEVEL_DATA_FILE)) { path ->
            NbtStreamUtils.write(data, Files.newOutputStream(path), true)
        }
    }


    companion object {

        private fun <R> readSafely(targetFile: Path, fn: (path: Path) -> R): R? {
            if (Files.exists(targetFile))
                return fn(targetFile)
            val oldFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_old")
            return if (Files.exists(oldFile)) fn(oldFile) else null
        }

        private fun <R> writeSafely(targetFile: Path, fn: (path: Path) -> R): R {
            val newFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_new")
            val oldFile = targetFile.parent.resolve(targetFile.fileName.toString() + "_old")
            val result = fn(newFile)
            if (Files.exists(oldFile))
                Files.delete(oldFile)
            if (Files.exists(targetFile))
                Files.move(targetFile, oldFile)
            Files.move(newFile, targetFile)
            return result
        }

        private fun DataView.addUniqueId(uniqueId: UUID) {
            set(UUID_MOST, uniqueId.mostSignificantBits)
            set(UUID_LEAST, uniqueId.leastSignificantBits)
        }

        private fun saveSpongeData(directory: Path, data: DataView) {
            val spongeDataFile = directory.resolve(SPONGE_LEVEL_DATA_FILE)
            val rootData = DataContainer.createNew()
                    .set(SPONGE_DATA, data)
            SafeIO.write(spongeDataFile) { path ->
                NbtStreamUtils.write(rootData, Files.newOutputStream(path), true)
            }
        }

        private fun loadSpongeData(directory: Path): DataView? {
            return readSafely(directory.resolve(SPONGE_LEVEL_DATA_FILE)) { path ->
                NbtStreamUtils.read(Files.newInputStream(path), true)
            }?.getView(SPONGE_DATA)?.orNull()
        }

        /**
         * Whether the directory name is reserved so it
         * can't be used as a world name.
         */
        fun isReservedDirectoryName(name: String): Boolean {
            return name == REGION_DIRECTORY_NAME
        }

        /**
         * Checks whether the target directory is considered a world directory.
         */
        fun isWorld(directory: Path): Boolean {
            if (!Files.exists(directory))
                return false
            if (Files.exists(directory.resolve(REGION_DIRECTORY_NAME)))
                return true
            if (Files.exists(directory.resolve(LEVEL_DATA_FILE)))
                return true
            return false
        }

        fun cleanupWorld(directory: Path) {
            Files.deleteIfExists(directory.resolve(REGION_DIRECTORY_NAME))
            // Not used, user data will be moved somewhere else
            Files.deleteIfExists(directory.resolve(LanternUserStorage.DATA_DIRECTORY))
            Files.deleteIfExists(directory.resolve(LanternUserStorage.ADVANCEMENTS_DATA_DIRECTORY))
            Files.deleteIfExists(directory.resolve(LanternUserStorage.STATISTICS_DATA_DIRECTORY))
            Files.deleteIfExists(directory.resolve(LanternUserStorage.SPONGE_DATA_DIRECTORY))
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

        const val DATA_DIRECTORY_NAME = "data"

        private const val SCOREBOARD_DATA_FILE = "scoreboard.dat"
        private const val LEVEL_DATA_FILE = "level.dat"
        private const val SPONGE_LEVEL_DATA_FILE = "level_sponge.dat"
        private const val BUKKIT_UUID_DATA_FILE = "uid.dat"
        private const val SESSION_LOCK_FILE = "session.lock"
        private const val REGION_DIRECTORY_NAME = "region"

        private val UUID_MOST = DataQuery.of("UUIDMost")
        private val UUID_LEAST = DataQuery.of("UUIDLeast")
        private val LEVEL_DATA = DataQuery.of("Level")
        private val SPONGE_DATA = DataQuery.of("SpongeData")
        private val SCOREBOARD = DataQuery.of("Scoreboard")
        private val DATA = DataQuery.of("data")
    }
}

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
package org.lanternpowered.server.service.user

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.service.user.UserStorage
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.data.persistence.json.JsonDataFormat
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils
import org.lanternpowered.server.util.SafeIO
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class LanternUserStorage(
        private val uniqueId: UUID,
        private val directory: Path
) : UserStorage {

    companion object {

        val DATA_DIRECTORY: Path = Paths.get("playerdata")
        val ADVANCEMENTS_DATA_DIRECTORY: Path = Paths.get("advancements")
        val STATISTICS_DATA_DIRECTORY: Path = Paths.get("stats")
        val SPONGE_DATA_DIRECTORY: Path = Paths.get("data", "sponge")

        val ADVANCEMENTS: DataQuery = DataQuery.of("Advancements")
        val STATISTICS: DataQuery = DataQuery.of("Statistics")
        val EXTENDED_SPONGE_DATA: DataQuery = DataQuery.of("ExtendedSpongeData")

        private fun getDataPath(uniqueId: UUID): Path = DATA_DIRECTORY.resolve("${uniqueId.toString().toLowerCase()}.dat")

        fun exists(uniqueId: UUID, directory: Path): Boolean = Files.exists(directory.resolve(getDataPath(uniqueId)))
    }

    override fun getUniqueId(): UUID = this.uniqueId

    private val lock = ReentrantReadWriteLock()

    private val dataPath: Path
        get() = this.directory.resolve(getDataPath(this.uniqueId))

    private val spongeDataPath: Path
        get() = this.directory.resolve(SPONGE_DATA_DIRECTORY).resolve("${uniqueId.toString().toLowerCase()}.dat")

    private val advancementsPath: Path
        get() = this.directory.resolve(ADVANCEMENTS_DATA_DIRECTORY).resolve("${uniqueId.toString().toLowerCase()}.json")

    private val statisticsPath: Path
        get() = this.directory.resolve(STATISTICS_DATA_DIRECTORY).resolve("${uniqueId.toString().toLowerCase()}.json")

    override val exists: Boolean
        get() = Files.exists(this.dataPath)

    override fun load(): DataContainer? = this.lock.read { this.load0() }

    private fun load0(): DataContainer? {
        var data: DataContainer? = null

        val dataPath = this.dataPath
        if (Files.exists(dataPath)) {
            data = Files.newInputStream(dataPath).use { input ->
                NbtStreamUtils.read(input, true)
            }
        }
        if (data == null)
            return null

        val spongeDataPath = this.spongeDataPath
        if (Files.exists(dataPath)) {
            try {
                val spongeData = Files.newInputStream(spongeDataPath).use { input ->
                    NbtStreamUtils.read(input, true)
                }
                data.set(EXTENDED_SPONGE_DATA, spongeData)
            } catch (t: Throwable) {
                LanternGame.logger.error("Failed to read sponge user data for: $uniqueId", t)
            }
        }

        fun readJsonData(query: DataQuery, path: Path) {
            if (Files.exists(path)) {
                Files.newBufferedReader(path).use { reader ->
                    val jsonData = JsonDataFormat.readContainer(JsonReader(reader))
                    data.set(query, jsonData)
                }
            }
        }

        readJsonData(ADVANCEMENTS, this.advancementsPath)
        readJsonData(STATISTICS, this.statisticsPath)

        return data
    }

    override fun save(data: DataView) = this.lock.write { this.save0(data) }

    private fun save0(data: DataView) {
        val advancementsData = data.getView(ADVANCEMENTS).orNull()
        val statisticsData = data.getView(STATISTICS).orNull()
        val spongeData = data.getView(EXTENDED_SPONGE_DATA).orNull()

        data.remove(ADVANCEMENTS)
        data.remove(STATISTICS)
        data.remove(EXTENDED_SPONGE_DATA)

        val dataPath = this.dataPath
        val advancementsPath = this.advancementsPath
        val statisticsPath = this.statisticsPath
        val spongeDataPath = this.spongeDataPath

        Files.createDirectories(dataPath.parent)
        Files.createDirectories(advancementsPath.parent)
        Files.createDirectories(statisticsPath.parent)

        SafeIO.write(spongeDataPath) { tmpPath ->
            Files.newOutputStream(tmpPath).use { output ->
                NbtStreamUtils.write(data, output, true)
            }
        }

        if (spongeData == null) {
            Files.deleteIfExists(spongeDataPath)
        } else {
            SafeIO.write(spongeDataPath) { tmpPath ->
                Files.newOutputStream(tmpPath).use { output ->
                    NbtStreamUtils.write(spongeData, output, true)
                }
            }
        }

        fun saveJsonData(jsonData: DataView?, path: Path) {
            if (jsonData == null) {
                Files.deleteIfExists(path)
            } else {
                SafeIO.write(spongeDataPath) { tmpPath ->
                    Files.newBufferedWriter(tmpPath).use { writer ->
                        JsonDataFormat.write(JsonWriter(writer), jsonData)
                    }
                }
            }
        }

        saveJsonData(advancementsData, advancementsPath)
        saveJsonData(statisticsData, statisticsPath)
    }

    override fun delete(): Boolean = this.lock.write { this.delete0() }

    private fun delete0(): Boolean {
        var success = false
        success = Files.deleteIfExists(this.dataPath) || success
        success = Files.deleteIfExists(this.spongeDataPath) || success
        success = Files.deleteIfExists(this.advancementsPath) || success
        success = Files.deleteIfExists(this.statisticsPath) || success
        return success
    }
}

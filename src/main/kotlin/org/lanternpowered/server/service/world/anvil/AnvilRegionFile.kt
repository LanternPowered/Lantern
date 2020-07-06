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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.server.service.world.anvil

import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.util.LongPacker
import org.slf4j.MarkerFactory
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.time.Instant
import java.util.BitSet
import kotlin.math.ceil

class AnvilRegionFile(
        val position: ChunkRegionPosition,
        val path: Path
) {

    private val file = RandomAccessFile(this.path.toFile(), "rw")
    private val directory = this.path.parent

    private val sectors = IntArray(SECTOR_BLOCK_INTS)
    private val usedSectors = BitSet()

    private val lock = Any()

    init {
        val offsetAndTimestamp = ChunkSector(0, 2)

        // Allocate the first two sector blocks for the offset and timestamp
        this.usedSectors.allocateChunkSector(offsetAndTimestamp)
        growIfNeeded(offsetAndTimestamp)

        // Read the offset table
        this.file.seek(offsetAndTimestamp)
        for (i in this.sectors.indices) {
            val sector = ChunkSector(this.file.readInt())
            this.usedSectors.allocateChunkSector(sector)
            this.sectors[i] = sector.packed
        }
    }

    /**
     * Gets all the chunk positions that have data.
     */
    val all: Collection<ChunkPosition>
        get() {
            synchronized(this.lock) {
                return allUnsafe()
            }
        }

    private fun allUnsafe(): Collection<ChunkPosition> {
        val list = mutableListOf<ChunkPosition>()
        for (index in this.sectors.indices) {
            val localPosition = LocalChunkPosition(index)
            if (getChunkSector(localPosition).exists)
                list += chunkPositionOf(this.position, localPosition)
        }
        return list
    }

    fun delete(position: ChunkPosition): Boolean {
        synchronized(this.lock) {
            return deleteUnsafe(position)
        }
    }

    private fun deleteUnsafe(position: ChunkPosition): Boolean {
        val locationPosition = position.toLocal()
        val sector = getChunkSector(locationPosition)
        if (!sector.exists)
            return false
        this.usedSectors.freeChunkSector(sector)
        setChunkSector(locationPosition, ChunkSector.NONE)
        setTimestamp(locationPosition, getTimestamp())
        Files.deleteIfExists(position.getExternalFile())
        return true
    }

    private fun getTimestamp(): Int = Instant.now().epochSecond.toInt()

    /**
     * Gets whether data exists at the given [ChunkPosition].
     */
    fun exists(position: ChunkPosition): Boolean {
        synchronized(this.lock) {
            return existsUnsafe(position)
        }
    }

    private fun existsUnsafe(position: ChunkPosition): Boolean {
        val locationPosition = position.toLocal()
        val sector = getChunkSector(locationPosition)
        return sector.exists
    }

    /**
     * Gets an input stream to read data for the given [ChunkPosition],
     * if the chunk exists.
     */
    fun getInputStream(position: ChunkPosition): InputStream? {
        synchronized(this.lock) {
            return getInputStreamUnsafe(position)
        }
    }

    /**
     * Clear all data from unused sectors, so that leftover compressed
     * code from previous chunks gets removed. This will reduce the world
     * size if it were to be zipped.
     */
    private fun cleanup() {
        // Calculate how many sectors blocks are available in the file
        val blocks = ceil(this.file.length().toDouble() / SECTOR_BLOCK_BYTES.toDouble()).toInt()
        // Loop through them and clear all non allocated sectors
        for (block in 0 until blocks) {
            if (this.usedSectors.get(block))
                continue
            this.file.seek(ChunkSector(block, 1))
            this.file.write(EMPTY_SECTOR_BLOCK)
        }
    }

    private fun getInputStreamUnsafe(position: ChunkPosition): InputStream? {
        val locationPosition = position.toLocal()

        val sector = getChunkSector(locationPosition)
        // The chunk doesn't have data
        if (!sector.exists)
            return null

        this.file.seek(sector)
        val length = this.file.readInt() - 1
        val formatAndExternal = this.file.readByte().toInt() and 0xff

        val data: ByteArray
        val formatId: Int

        // Check if it's an external file
        if ((formatAndExternal and EXTERNAL_FILE_MASK) != 0) {
            if (length == 0)
                Lantern.getLogger().warn(REGION_FILE_MARKER, "Reading chunk $position: normal and external data was found.")

            val file = position.getExternalFile()
            if (!Files.isRegularFile(file))
                return null

            formatId = formatAndExternal and EXTERNAL_FILE_MASK.inv()
            data = Files.readAllBytes(file)
        } else {
            formatId = formatAndExternal

            data = ByteArray(length)
            // Read the data locally, the length should never be empty
            val read = this.file.read(data)
            if (read == -1) {
                Lantern.getLogger().warn(REGION_FILE_MARKER, "Reading chunk $position: expected data, but nothing was found.")
                return null
            } else if (read != length) {
                Lantern.getLogger().warn(REGION_FILE_MARKER, "Reading chunk $position: expected $length bytes, but got $read.")
                return null
            }
        }

        val format = AnvilChunkSectorFormat[formatId]
        if (format == null) {
            Lantern.getLogger().warn(REGION_FILE_MARKER, "Reading chunk $position: unsupported chunk format $formatId.")
            return null
        }

        val input = ByteArrayInputStream(data)
        return BufferedInputStream(format.inputTransformer(input))
    }

    /**
     * Gets an output stream to write data for the given [ChunkPosition].
     */
    fun getOutputStream(position: ChunkPosition, format: AnvilChunkSectorFormat = AnvilChunkSectorFormat.Zlib): OutputStream =
            BufferedOutputStream(format.outputTransformer(ChunkBuffer(position, format.id)))

    private inner class ChunkBuffer internal constructor(
            private val position: ChunkPosition,
            private val format: Int
    ) : ByteArrayOutputStream(8192) {

        override fun close() {
            try {
                synchronized(lock) {
                    write(this.position, this.format, this.buf, this.count)
                }
            } finally {
                super.close()
            }
        }
    }

    private fun write(position: ChunkPosition, format: Int, data: ByteArray, length: Int) {
        // Convert to local coordinates
        val localPosition = position.toLocal()

        // The sector where the chunk data was previously written
        val oldSector = getChunkSector(localPosition)

        // Calculate the size of the sector
        val neededSectorSize = ceil((length + CHUNK_HEADER_SIZE).toDouble() / SECTOR_BLOCK_BYTES.toDouble()).toInt()

        // The sector size is too big, > 1MB, chunks that are
        // too big will be written to a separate file.
        val sector: ChunkSector
        val cleanup: () -> Unit
        if (neededSectorSize >= 256) {
            // Allocate one section to store the header
            sector = this.usedSectors.allocateChunkSector(1)
            writeExternal(position, sector, format, data, length)
            cleanup = {}
        } else {
            sector = this.usedSectors.allocateChunkSector(neededSectorSize)
            cleanup = writeLocal(position, sector, format, data, length)
        }

        setChunkSector(localPosition, sector)
        setTimestamp(localPosition, getTimestamp())

        // Everything was successful, so cleanup
        cleanup()
        this.usedSectors.freeChunkSector(oldSector)
    }

    /**
     * Moves the file pointer to the given chunk sector.
     */
    private fun RandomAccessFile.seek(sector: ChunkSector) {
        seek(sector.index * SECTOR_BLOCK_BYTES.toLong())
    }

    /**
     * Writes chunk data to an external file.
     */
    private fun writeExternal(position: ChunkPosition, chunkSector: ChunkSector, format: Int, data: ByteArray, length: Int) {
        growIfNeeded(chunkSector)

        this.file.seek(chunkSector)
        this.file.writeInt(1)
        this.file.writeByte(format or EXTERNAL_FILE_MASK)

        val path = position.getExternalFile()
        val tempPath = Files.createTempFile(this.directory, "tmp", null)

        // Write the external file, to a temp file in case of errors,
        // this file doesn't contain headers, just chunk data
        Files.newOutputStream(tempPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE).use { os ->
            os.write(data, 0, length)
            os.flush()
        }

        Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING)
    }

    /**
     * Writes chunk data at the given chunk sector.
     */
    private fun writeLocal(position: ChunkPosition, chunkSector: ChunkSector, format: Int, data: ByteArray, length: Int): () -> Unit {
        growIfNeeded(chunkSector)

        this.file.seek(chunkSector)
        this.file.writeInt(length + 1)
        this.file.writeByte(format)
        this.file.write(data, 0, length)

        // Remove the external file, if it exists
        val path = position.getExternalFile()
        return {
            Files.deleteIfExists(path)
        }
    }

    /**
     * Gets the path for the external file of the chunk position.
     */
    private fun ChunkPosition.getExternalFile(): Path = directory.resolve("c.$x.$z.$EXTERNAL_FILE_EXTENSION")

    /**
     * Grows the file if it's needed.
     */
    private fun growIfNeeded(chunkSector: ChunkSector) {
        val endIndex = (chunkSector.index + chunkSector.size) * SECTOR_BLOCK_BYTES.toLong()

        // Grow the file, it's not big enough
        if (this.file.length() < endIndex) {
            this.file.seek(this.file.length())
            while (this.file.length() < endIndex) {
                this.file.write(EMPTY_SECTOR_BLOCK)
            }

            // If the file size is not a multiple of 4KB, grow it
            val length = this.file.length().toInt()
            if (length and 0xfff != 0) {
                val remaining = SECTOR_BLOCK_BYTES - (length and 0xfff)
                Lantern.getLogger().warn(REGION_FILE_MARKER,
                        "Region $position not aligned: $length increasing by $remaining")
                repeat(remaining) {
                    this.file.write(0)
                }
            }
        }
    }

    /**
     * Gets the chunk sector for the given local coordinates.
     */
    private fun getChunkSector(position: LocalChunkPosition): ChunkSector =
            ChunkSector(this.sectors[position.packed])

    /**
     * Sets the chunk sector for the chunk at the given coordinates.
     */
    private fun setChunkSector(position: LocalChunkPosition, sector: ChunkSector) {
        this.sectors[position.packed] = sector.packed
        this.file.seek(position.packed * Int.SIZE_BYTES.toLong())
        this.file.writeInt(sector.packed)
    }

    /**
     * Sets the timestamp for the chunk at the given coordinates.
     */
    private fun setTimestamp(position: LocalChunkPosition, value: Int) {
        this.file.seek(SECTOR_BLOCK_BYTES + position.packed * Int.SIZE_BYTES.toLong())
        this.file.writeInt(value)
    }

    /**
     * Closes this region file.
     */
    fun close(fast: Boolean = false) {
        synchronized(this.lock) {
            if (!fast)
                cleanup()
            this.file.channel.force(true)
            this.file.close()
        }
    }

    companion object {

        const val REGION_FILE_EXTENSION = "mca"
        const val REGION_COORDINATE_BITS = 5
        private const val REGION_SIZE = 1 shl REGION_COORDINATE_BITS
        const val REGION_MASK = REGION_SIZE - 1
        const val EXTERNAL_FILE_MASK = 0x80
        const val EXTERNAL_FILE_EXTENSION = "mcc"

        private const val CHUNK_HEADER_SIZE = 5
        private const val SECTOR_BLOCK_BYTES = 4096
        private const val SECTOR_BLOCK_INTS = SECTOR_BLOCK_BYTES / Int.SIZE_BYTES
        private val EMPTY_SECTOR_BLOCK = ByteArray(SECTOR_BLOCK_BYTES)

        private val REGION_FILE_MARKER = MarkerFactory.getMarker("REGION_FILE")
    }
}

/**
 * Converts the chunk position to a local chunk position.
 */
private fun ChunkPosition.toLocal(): LocalChunkPosition {
    val localX = this.x and AnvilRegionFile.REGION_MASK
    val localZ = this.z and AnvilRegionFile.REGION_MASK
    return LocalChunkPosition(localX, localZ)
}

/**
 * Allocates a chunk sector.
 */
private fun BitSet.allocateChunkSector(size: Int): ChunkSector {
    val sector = findFreeChunkSector(size)
    allocateChunkSector(sector)
    return sector
}

/**
 * Allocates the chunk sector.
 */
private fun BitSet.allocateChunkSector(sector: ChunkSector) {
    if (!sector.exists)
        return
    val (index, size) = sector
    clear(index, index + size - 1)
}

/**
 * Clears a chunk sector.
 */
private fun BitSet.freeChunkSector(sector: ChunkSector) {
    if (!sector.exists)
        return
    val (index, size) = sector
    clear(index, index + size - 1)
}

/**
 * Finds the start index of a sector that's free and big enough.
 */
private tailrec fun BitSet.findFreeChunkSector(size: Int, offset: Int = 0): ChunkSector {
    val start = nextClearBit(offset)
    val end = nextSetBit(start + 1)
    return if (end == -1 || end - start >= size) ChunkSector(start, size) else findFreeChunkSector(size, end)
}

/**
 * Converts the chunk position to a chunk region position.
 */
fun ChunkPosition.toRegion(): ChunkRegionPosition {
    val regionX = this.x shr AnvilRegionFile.REGION_COORDINATE_BITS
    val regionZ = this.z shr AnvilRegionFile.REGION_COORDINATE_BITS
    return ChunkRegionPosition(regionX, regionZ)
}

/**
 * Constructs a new chunk position.
 */
private fun chunkPositionOf(regionPosition: ChunkRegionPosition, localPosition: LocalChunkPosition): ChunkPosition {
    val x = (regionPosition.x shl AnvilRegionFile.REGION_COORDINATE_BITS) or localPosition.x
    val z = (regionPosition.z shl AnvilRegionFile.REGION_COORDINATE_BITS) or localPosition.z
    return ChunkPosition(x, z)
}

/**
 * Represents a chunk region.
 */
inline class ChunkRegionPosition(val packed: Long) {

    /**
     * Constructs a new local chunk position.
     */
    constructor(x: Int, z: Int) : this(LongPacker.pack(x, z))

    /**
     * The x coordinate.
     */
    val x: Int get() = LongPacker.unpackInt1(this.packed)

    /**
     * The z coordinate.
     */
    val z: Int get() = LongPacker.unpackInt2(this.packed)

    inline operator fun component1(): Int = this.x
    inline operator fun component2(): Int = this.z

    override fun toString(): String = "($x, $z)"
}

/**
 * Represents a chunk position within a region file.
 */
private inline class LocalChunkPosition(val packed: Int) {

    /**
     * Constructs a new local chunk position.
     */
    constructor(x: Int, z: Int) : this(x or (z shl AnvilRegionFile.REGION_COORDINATE_BITS))

    /**
     * The x coordinate.
     */
    val x: Int get() = this.packed and AnvilRegionFile.REGION_MASK

    /**
     * The z coordinate.
     */
    val z: Int get() = this.packed shr AnvilRegionFile.REGION_COORDINATE_BITS

    inline operator fun component1(): Int = this.x
    inline operator fun component2(): Int = this.z

    override fun toString(): String = "($x, $z)"
}

/**
 * Represents a chunk sector. This sector start position and
 * the number of sectors being used for the chunk.
 */
private inline class ChunkSector(val packed: Int) {

    /**
     * Constructs a new [ChunkSector].
     */
    constructor(index: Int, size: Int) : this((index shl 8) or size)

    /**
     * Whether the sector exists.
     */
    val exists: Boolean get() = this.packed != NONE.packed

    /**
     * The sector start index.
     */
    val index: Int get() = this.packed shr 8

    /**
     * The size of the chunk sector.
     */
    val size: Int get() = this.packed and 0xff

    inline operator fun component1(): Int = this.index
    inline operator fun component2(): Int = this.size

    override fun toString(): String = if (this.exists) "ChunkSector(index=$index,size=$size)" else "ChunkSector(empty)"

    companion object {

        val NONE = ChunkSector(0)
    }
}

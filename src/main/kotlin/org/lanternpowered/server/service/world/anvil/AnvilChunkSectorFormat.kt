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
package org.lanternpowered.server.service.world.anvil

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.jpountz.lz4.LZ4BlockInputStream
import net.jpountz.lz4.LZ4BlockOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.InflaterInputStream

/**
 * Represents a format in which chunk data will be saved.
 */
sealed class AnvilChunkSectorFormat(
        val id: Int,
        val inputTransformer: (InputStream) -> InputStream,
        val outputTransformer: (OutputStream) -> OutputStream
) {

    object Gzip : AnvilChunkSectorFormat(1, ::GZIPInputStream, ::GZIPOutputStream)
    object Zlib : AnvilChunkSectorFormat(2, ::InflaterInputStream, { DeflaterOutputStream(it, Deflater(Deflater.BEST_SPEED)) })
    object Uncompressed : AnvilChunkSectorFormat(3, { it }, { it })
    object Lz4 : AnvilChunkSectorFormat(10, ::LZ4BlockInputStream, { LZ4BlockOutputStream(it, 1024) }) // TODO: Best block size?

    companion object {

        private val lookup = Int2ObjectOpenHashMap<AnvilChunkSectorFormat>().apply {
            for (format in arrayOf(Gzip, Zlib, Uncompressed, Lz4))
                put(format.id, format)
        }

        operator fun get(id: Int): AnvilChunkSectorFormat? = this.lookup[id]
    }
}

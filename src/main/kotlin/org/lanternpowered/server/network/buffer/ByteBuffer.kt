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
package org.lanternpowered.server.network.buffer

import io.netty.handler.codec.DecoderException
import io.netty.util.ReferenceCounted
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.item.RawItemStack
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.network.channel.ChannelBuf
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3f
import org.spongepowered.math.vector.Vector3i
import java.util.UUID

interface ByteBuffer : ChannelBuf, ReferenceCounted {

    override fun ensureWritable(minWritableBytes: Int): ByteBuffer
    override fun retain(): ByteBuffer
    override fun retain(increment: Int): ByteBuffer
    override fun touch(): ByteBuffer
    override fun touch(hint: Any): ByteBuffer
    override fun readerIndex(index: Int): ByteBuffer
    override fun writerIndex(index: Int): ByteBuffer
    override fun setIndex(readIndex: Int, writeIndex: Int): ByteBuffer
    override fun clear(): ByteBuffer
    override fun slice(): ByteBuffer
    override fun slice(index: Int, length: Int): ByteBuffer

    override fun writeBoolean(data: Boolean): ByteBuffer
    override fun setBoolean(index: Int, data: Boolean): ByteBuffer

    override fun writeByte(data: Byte): ByteBuffer
    override fun setByte(index: Int, data: Byte): ByteBuffer

    /**
     * Reads a byte array and checks whether the length isn't
     * longer then the specified maximum length.
     *
     * @param maxLength The maximum length
     * @return The byte array
     * @throws DecoderException If the length of the byte array
     *                          exceeded the specified maximum length
     */
    fun readLimitedByteArray(maxLength: Int): ByteArray

    override fun writeByteArray(data: ByteArray): ByteBuffer
    override fun writeByteArray(data: ByteArray, start: Int, length: Int): ByteBuffer
    override fun setByteArray(index: Int, data: ByteArray): ByteBuffer
    override fun setByteArray(index: Int, data: ByteArray, start: Int, length: Int): ByteBuffer

    override fun writeShort(data: Short): ByteBuffer
    override fun writeShortLE(data: Short): ByteBuffer
    override fun setShort(index: Int, data: Short): ByteBuffer
    override fun setShortLE(index: Int, data: Short): ByteBuffer

    override fun writeChar(data: Char): ByteBuffer
    override fun setChar(index: Int, data: Char): ByteBuffer

    override fun writeInt(data: Int): ByteBuffer
    override fun writeIntLE(data: Int): ByteBuffer
    override fun setInt(index: Int, data: Int): ByteBuffer
    override fun setIntLE(index: Int, data: Int): ByteBuffer

    override fun writeLong(data: Long): ByteBuffer
    override fun writeLongLE(data: Long): ByteBuffer
    override fun setLong(index: Int, data: Long): ByteBuffer
    override fun setLongLE(index: Int, data: Long): ByteBuffer

    override fun writeFloat(data: Float): ByteBuffer
    override fun writeFloatLE(data: Float): ByteBuffer
    override fun setFloat(index: Int, data: Float): ByteBuffer
    override fun setFloatLE(index: Int, data: Float): ByteBuffer

    override fun writeDouble(data: Double): ByteBuffer
    override fun writeDoubleLE(data: Double): ByteBuffer
    override fun setDouble(index: Int, data: Double): ByteBuffer
    override fun setDoubleLE(index: Int, data: Double): ByteBuffer

    override fun writeVarInt(value: Int): ByteBuffer
    override fun setVarInt(index: Int, value: Int): ByteBuffer

    override fun writeVarLong(value: Long): ByteBuffer
    override fun setVarLong(index: Int, value: Long): ByteBuffer

    /**
     * Reads a string and checks whether the length isn't longer
     * then the specified maximum length.
     *
     * @param maxLength The maximum length
     * @return The string
     * @throws DecoderException If the length of the string
     *                          exceeded the specified maximum length
     */
    fun readLimitedString(maxLength: Int): String

    override fun writeString(data: String): ByteBuffer
    override fun setString(index: Int, data: String): ByteBuffer
    override fun writeUTF(data: String): ByteBuffer
    override fun setUTF(index: Int, data: String): ByteBuffer
    override fun writeUniqueId(data: UUID): ByteBuffer
    override fun setUniqueId(index: Int, data: UUID): ByteBuffer

    override fun writeDataView(data: DataView?): ByteBuffer
    override fun setDataView(index: Int, data: DataView?): ByteBuffer

    /**
     * Reads a [DataView] and checks whether the depth of the underlying
     * tree doesn't get bigger then the specified maximum depth and is not bigger
     * then the maximum amount of bytes.
     *
     * @param maximumDepth The maximum depth
     * @param maximumBytes The maximum amount of bytes
     * @return The data view
     * @throws IOException If the depth exceeded the maximum depth or
     *                     if the size exceeded the maximum bytes
     */
    fun readLimitedDataView(maximumDepth: Int, maximumBytes: Int): DataView?

    override fun readDataView(): DataView?
    override fun getDataView(index: Int): DataView?

    fun writeBytes(byteBuffer: ByteBuffer): ByteBuffer
    override fun writeBytes(data: ByteArray): ByteBuffer
    override fun writeBytes(data: ByteArray, start: Int, length: Int): ByteBuffer
    override fun setBytes(index: Int, data: ByteArray): ByteBuffer
    override fun setBytes(index: Int, data: ByteArray, start: Int, length: Int): ByteBuffer

    /**
     * Transfers this buffer's data to the specified byte array, starting
     * from the current readerIndex. Transferring until either this buffer
     * is at the end of the reader index or the specified array is filled.
     *
     * @param dst The destination byte array
     * @return This stream for chaining
     */
    fun readBytes(dst: ByteArray): ByteBuffer
    fun readBytes(dst: ByteArray, dstIndex: Int, length: Int): ByteBuffer

    /**
     * Transfers this buffer's data to the specified byte buffer, starting
     * from the current readerIndex.
     *
     * @param dst The destination byte buffer
     * @return This stream for chaining
     */
    fun readBytes(dst: ByteBuffer): ByteBuffer
    fun readBytes(dst: ByteBuffer, dstIndex: Int, length: Int): ByteBuffer

    fun getVector3i(index: Int): Vector3i
    fun setVector3i(index: Int, vector: Vector3i): ByteBuffer
    fun readVector3i(): Vector3i
    fun writeVector3i(x: Int, y: Int, z: Int): ByteBuffer
    fun writeVector3i(vector: Vector3i): ByteBuffer

    fun getBlockPosition(index: Int): Vector3i
    fun setBlockPosition(index: Int, vector: Vector3i): ByteBuffer
    fun readBlockPosition(): Vector3i
    fun writeBlockPosition(x: Int, y: Int, z: Int): ByteBuffer
    fun writeBlockPosition(vector: Vector3i): ByteBuffer

    fun getVector3f(index: Int): Vector3f
    fun setVector3f(index: Int, vector: Vector3f): ByteBuffer
    fun readVector3f(): Vector3f
    fun writeVector3f(x: Float, y: Float, z: Float): ByteBuffer
    fun writeVector3f(vector: Vector3f): ByteBuffer

    fun getVector3d(index: Int): Vector3d
    fun setVector3d(index: Int, vector: Vector3d): ByteBuffer
    fun readVector3d(): Vector3d
    fun writeVector3d(x: Double, y: Double, z: Double): ByteBuffer
    fun writeVector3d(vector: Vector3d): ByteBuffer

    fun getNamespacedKey(index: Int): NamespacedKey
    fun setNamespacedKey(index: Int, namespacedKey: NamespacedKey): ByteBuffer
    fun readNamespacedKey(): NamespacedKey
    fun writeNamespacedKey(namespacedKey: NamespacedKey): ByteBuffer

    fun getRawItemStack(index: Int): RawItemStack?
    fun setRawItemStack(index: Int, rawItemStack: RawItemStack?): ByteBuffer
    fun readRawItemStack(): RawItemStack?
    fun writeRawItemStack(rawItemStack: RawItemStack?): ByteBuffer

    /**
     * Writes the specified value for the [ContextualValueCodec]. The value may be
     * `null` depending on the value type.
     *
     * @param context The context
     * @param type The type
     * @param value The value
     * @param V The value type
     */
    fun <V> write(context: CodecContext, type: ContextualValueCodec<V>, value: V)

    fun copy(): ByteBuffer
}

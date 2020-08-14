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

import com.google.common.io.ByteStreams
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.server.data.persistence.nbt.NbtDataContainerInputStream
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils
import org.lanternpowered.server.network.item.NetworkItemHelper
import org.lanternpowered.server.network.item.RawItemStack
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3f
import org.spongepowered.math.vector.Vector3i
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.OptionalInt
import java.util.UUID

class LanternByteBuffer(val delegate: ByteBuf) : ByteBuffer {

    override fun capacity(): Int = this.delegate.capacity()
    override fun available(): Int = this.delegate.readableBytes()
    override fun refCnt(): Int = this.delegate.refCnt()

    override fun ensureWritable(minWritableBytes: Int): LanternByteBuffer =
            apply { this.delegate.ensureWritable(minWritableBytes) }

    override fun release(): Boolean = this.delegate.release()
    override fun release(decrement: Int): Boolean = this.delegate.release(decrement)

    override fun asOutputStream(): OutputStream = ByteBufOutputStream(this.delegate)
    override fun asInputStream(): InputStream = ByteBufInputStream(this.delegate)

    override fun retain(): ByteBuffer = this.apply { this.delegate.retain() }
    override fun retain(increment: Int): ByteBuffer = this.apply { this.delegate.retain(increment) }
    override fun touch(): ByteBuffer = this.apply { this.delegate.touch() }
    override fun touch(hint: Any): ByteBuffer = this.apply { this.delegate.touch(hint) }

    override fun readerIndex(): Int = this.delegate.readerIndex()

    override fun readerIndex(index: Int): LanternByteBuffer =
            this.apply { this.delegate.readerIndex(index) }

    override fun writerIndex(): Int = this.delegate.writerIndex()

    override fun writerIndex(index: Int): LanternByteBuffer =
            this.apply { this.delegate.writerIndex(index) }

    override fun setIndex(readIndex: Int, writeIndex: Int): LanternByteBuffer =
            this.apply { this.delegate.setIndex(readIndex, writeIndex) }

    override fun clear(): LanternByteBuffer =
            this.apply { this.delegate.clear() }

    override fun copy(): LanternByteBuffer =
            LanternByteBuffer(this.delegate.copy())

    override fun slice(): LanternByteBuffer =
            LanternByteBuffer(this.delegate.slice())

    override fun slice(index: Int, length: Int): LanternByteBuffer =
            LanternByteBuffer(this.delegate.slice(index, length))

    override fun readSlice(length: Int): LanternByteBuffer =
            LanternByteBuffer(this.delegate.readSlice(length))

    override fun hasArray(): Boolean = this.delegate.hasArray()
    override fun array(): ByteArray = this.delegate.array()

    override fun writeBoolean(data: Boolean): LanternByteBuffer =
            this.apply { this.delegate.writeBoolean(data) }

    override fun setBoolean(index: Int, data: Boolean): LanternByteBuffer =
            this.apply { this.delegate.setBoolean(index, data) }

    override fun readBoolean(): Boolean = this.delegate.readBoolean()

    override fun getBoolean(index: Int): Boolean = this.delegate.getBoolean(index)

    override fun writeByte(data: Byte): LanternByteBuffer =
            this.apply { this.delegate.writeByte(data.toInt()) }

    override fun setByte(index: Int, data: Byte): LanternByteBuffer =
            this.apply { this.delegate.setByte(index, data.toInt()) }

    override fun readByte(): Byte = this.delegate.readByte()
    override fun getByte(index: Int): Byte = this.delegate.getByte(index)

    override fun writeByteArray(data: ByteArray): LanternByteBuffer = this.apply {
        this.writeVarInt(data.size)
        this.writeBytes(data)
    }

    override fun writeByteArray(data: ByteArray, start: Int, length: Int): LanternByteBuffer = this.apply {
        this.writeVarInt(length)
        this.writeBytes(data, start, length)
    }

    override fun setByteArray(index: Int, data: ByteArray): LanternByteBuffer =
            this.writeAt(index) { this.writeByteArray(data) }

    override fun setByteArray(index: Int, data: ByteArray, start: Int, length: Int): LanternByteBuffer =
            this.writeAt(index) {
                this.writeVarInt(length)
                this.writeBytes(data, start, length)
            }

    override fun readLimitedByteArray(maxLength: Int): ByteArray {
        val length = this.readVarInt()
        if (length < 0)
            throw DecoderException("Byte array length may not be negative.")
        if (length > maxLength)
            throw DecoderException("Exceeded the maximum allowed length, got $length which is greater then $maxLength")
        val bytes = ByteArray(length)
        this.delegate.readBytes(bytes)
        return bytes
    }

    override fun readByteArray(): ByteArray =
            this.readLimitedByteArray(Int.MAX_VALUE)

    override fun readByteArray(limit: Int): ByteArray =
            this.readLimitedByteArray(limit)

    override fun getByteArray(index: Int): ByteArray =
            this.readAt(index) { this.readByteArray() }

    override fun getByteArray(index: Int, limit: Int): ByteArray =
            this.readAt(index) { this.readLimitedByteArray(limit) }

    override fun writeBytes(data: ByteArray): LanternByteBuffer =
            this.apply { this.delegate.writeBytes(data) }

    override fun writeBytes(data: ByteArray, start: Int, length: Int): LanternByteBuffer =
            this.apply { this.delegate.writeBytes(data, start, length) }

    override fun setBytes(index: Int, data: ByteArray): LanternByteBuffer =
            this.writeAt(index) { this.writeBytes(data) }

    override fun setBytes(index: Int, data: ByteArray, start: Int, length: Int): LanternByteBuffer =
            this.writeAt(index) { this.writeBytes(data, start, length) }

    override fun readBytes(length: Int): ByteArray {
        val data = ByteArray(length)
        this.delegate.readBytes(data)
        return data
    }

    override fun readBytes(index: Int, length: Int): ByteArray =
            this.readAt(index) { this.readBytes(length) }

    override fun writeShort(data: Short): LanternByteBuffer =
            this.apply { this.delegate.writeShort(data.toInt()) }

    override fun writeShortLE(data: Short): LanternByteBuffer =
            this.apply { this.delegate.writeShortLE(data.toInt()) }

    override fun setShort(index: Int, data: Short): LanternByteBuffer =
            this.apply { this.delegate.setShort(index, data.toInt()) }

    override fun setShortLE(index: Int, data: Short): ByteBuffer =
            this.apply { this.delegate.setShortLE(index, data.toInt()) }

    override fun readShort(): Short = this.delegate.readShort()
    override fun readShortLE(): Short = this.delegate.readShortLE()
    override fun getShort(index: Int): Short = this.delegate.getShort(index)
    override fun getShortLE(index: Int): Short = this.delegate.getShortLE(index)

    override fun writeChar(data: Char): LanternByteBuffer =
            this.apply { this.delegate.writeChar(data.toInt()) }

    override fun setChar(index: Int, data: Char): LanternByteBuffer =
            this.apply { this.delegate.setChar(index, data.toInt()) }

    override fun readChar(): Char = this.delegate.readChar()
    override fun getChar(index: Int): Char = this.delegate.getChar(index)

    override fun writeInt(data: Int): LanternByteBuffer =
            this.apply { this.delegate.writeInt(data) }

    override fun writeIntLE(data: Int): LanternByteBuffer =
            this.apply { this.delegate.writeIntLE(data) }

    override fun setInt(index: Int, data: Int): LanternByteBuffer =
            this.apply { this.delegate.setInt(index, data) }

    override fun setIntLE(index: Int, data: Int): LanternByteBuffer =
            this.apply { this.delegate.setIntLE(index, data) }

    override fun readInt(): Int = this.delegate.readInt()
    override fun readIntLE(): Int = this.delegate.readIntLE()
    override fun getInt(index: Int): Int = this.delegate.getInt(index)
    override fun getIntLE(index: Int): Int = this.delegate.getIntLE(index)

    override fun writeLong(data: Long): LanternByteBuffer =
            this.apply { this.delegate.writeLong(data) }

    override fun writeLongLE(data: Long): LanternByteBuffer =
            this.apply { this.delegate.writeLongLE(data) }

    override fun setLong(index: Int, data: Long): LanternByteBuffer =
            this.apply { this.delegate.setLong(index, data) }

    override fun setLongLE(index: Int, data: Long): LanternByteBuffer =
            this.apply { this.delegate.setLongLE(index, data) }

    override fun readLong(): Long = this.delegate.readLong()
    override fun readLongLE(): Long = this.delegate.readLongLE()
    override fun getLong(index: Int): Long = this.delegate.getLong(index)
    override fun getLongLE(index: Int): Long = this.delegate.getLongLE(index)

    override fun writeFloat(data: Float): LanternByteBuffer =
            this.apply { this.delegate.writeFloat(data) }

    override fun writeFloatLE(data: Float): LanternByteBuffer =
            this.apply { this.delegate.writeFloatLE(data) }

    override fun setFloat(index: Int, data: Float): LanternByteBuffer =
            this.apply { this.delegate.setFloat(index, data) }

    override fun setFloatLE(index: Int, data: Float): LanternByteBuffer =
            this.apply { this.delegate.setFloatLE(index, data) }

    override fun readFloat(): Float = this.delegate.readFloat()
    override fun readFloatLE(): Float = this.delegate.readFloatLE()
    override fun getFloat(index: Int): Float = this.delegate.getFloat(index)
    override fun getFloatLE(index: Int): Float = this.delegate.getFloatLE(index)

    override fun writeDouble(data: Double): LanternByteBuffer =
            this.apply { this.delegate.writeDouble(data) }

    override fun writeDoubleLE(data: Double): LanternByteBuffer =
            this.apply { this.delegate.writeDoubleLE(data) }

    override fun setDouble(index: Int, data: Double): LanternByteBuffer =
            this.apply { this.delegate.setDouble(index, data) }

    override fun setDoubleLE(index: Int, data: Double): LanternByteBuffer =
            this.apply { this.delegate.setDoubleLE(index, data) }

    override fun readDouble(): Double = this.delegate.readDouble()
    override fun readDoubleLE(): Double = this.delegate.readDoubleLE()
    override fun getDouble(index: Int): Double = this.delegate.getDouble(index)
    override fun getDoubleLE(index: Int): Double = this.delegate.getDoubleLE(index)

    override fun writeVarInt(value: Int): LanternByteBuffer =
            this.apply { writeVarInt(this.delegate, value) }

    override fun setVarInt(index: Int, value: Int): LanternByteBuffer =
            this.writeAt(index) { this.writeVarInt(value) }

    override fun readVarInt(): Int = readVarInt(this.delegate)

    override fun getVarInt(index: Int): Int =
            this.readAt(index) { this.readVarInt() }

    override fun writeString(data: String): LanternByteBuffer =
            this.writeByteArray(data.toByteArray(UTF_8))

    override fun setString(index: Int, data: String): LanternByteBuffer =
            this.setByteArray(index, data.toByteArray(UTF_8))

    override fun readLimitedString(maxLength: Int): String =
            String(this.readLimitedByteArray(maxLength * 4), UTF_8)

    override fun readString(): String =
            this.readLimitedString(Short.MAX_VALUE.toInt())

    override fun getString(index: Int): String =
            String(this.readByteArray(index), UTF_8)

    override fun writeUTF(data: String): LanternByteBuffer = this.apply {
        val bytes = data.toByteArray(UTF_8)
        if (bytes.size > Short.MAX_VALUE)
            throw EncoderException("String too big (was ${data.length} bytes encoded, max ${Short.MAX_VALUE})")
        this.delegate.writeShort(bytes.size)
        this.delegate.writeBytes(bytes)
    }

    override fun setUTF(index: Int, data: String): LanternByteBuffer =
            this.writeAt(index) { this.writeUTF(data) }

    override fun readUTF(): String {
        val length = this.readShort().toInt()
        return String(this.readBytes(length), UTF_8)
    }

    override fun getUTF(index: Int): String =
            this.readAt(index) { this.readUTF() }

    override fun writeUniqueId(data: UUID): LanternByteBuffer {
        this.delegate.writeLong(data.mostSignificantBits)
        this.delegate.writeLong(data.leastSignificantBits)
        return this
    }

    override fun setUniqueId(index: Int, data: UUID): LanternByteBuffer =
            this.writeAt(index) { this.writeUniqueId(data) }

    override fun readUniqueId(): UUID {
        val most = this.delegate.readLong()
        val least = this.delegate.readLong()
        return UUID(most, least)
    }

    override fun getUniqueId(index: Int): UUID =
            this.readAt(index) { this.readUniqueId() }

    override fun writeDataView(data: DataView?): LanternByteBuffer {
        if (data == null) {
            this.delegate.writeByte(0)
            return this
        }
        try {
            NbtStreamUtils.write(data, ByteBufOutputStream(this.delegate), false)
        } catch (t: Throwable) {
            throw EncoderException("Failed to write DataView", t)
        }
        return this
    }

    override fun setDataView(index: Int, data: DataView?): LanternByteBuffer =
            this.writeAt(index) { this.writeDataView(data) }

    override fun readLimitedDataView(maximumDepth: Int, maximumBytes: Int): DataView? {
        val index = this.delegate.readerIndex()
        if (this.delegate.readByte().toInt() == 0)
            return null
        this.delegate.readerIndex(index)
        try {
            val input = ByteBufInputStream(this.delegate)
            val limited = ByteStreams.limit(input, maximumBytes.toLong())
            return NbtDataContainerInputStream(limited, maximumDepth)
                    .use { containerInput -> containerInput.read() }
        } catch (t: Throwable) {
            throw DecoderException("Failed to read DataView", t)
        }
    }

    override fun readDataView(): DataView? = this.readLimitedDataView(Int.MAX_VALUE, Int.MAX_VALUE)
    override fun getDataView(index: Int): DataView? = this.readAt(index) { this.readDataView() }

    override fun writeBytes(byteBuffer: ByteBuffer): LanternByteBuffer =
            this.apply { this.delegate.writeBytes((byteBuffer as LanternByteBuffer).delegate) }

    override fun readBytes(dst: ByteArray): LanternByteBuffer =
            this.apply { this.delegate.readBytes(dst) }

    override fun readBytes(dst: ByteArray, dstIndex: Int, length: Int): LanternByteBuffer =
            this.apply { this.delegate.readBytes(dst, dstIndex, length) }

    override fun readBytes(dst: ByteBuffer): LanternByteBuffer =
            this.apply { this.delegate.readBytes((dst as LanternByteBuffer).delegate) }

    override fun readBytes(dst: ByteBuffer, dstIndex: Int, length: Int): ByteBuffer =
            this.apply { this.delegate.readBytes((dst as LanternByteBuffer).delegate, dstIndex, length) }

    override fun writeVarLong(value: Long): LanternByteBuffer =
            this.apply { writeVarLong(this.delegate, value) }

    override fun setVarLong(index: Int, value: Long): LanternByteBuffer =
            this.writeAt(index) { this.writeVarLong(value) }

    override fun readVarLong(): Long = readVarLong(this.delegate)

    override fun getVarLong(index: Int): Long =
            this.readAt(index) { this.readVarLong() }

    override fun getVector3i(index: Int): Vector3i =
            this.readAt(index) { this.readVector3i() }

    override fun setVector3i(index: Int, vector: Vector3i): LanternByteBuffer =
            this.writeAt(index) { this.writeVector3i(vector) }

    override fun readVector3i(): Vector3i {
        val x = this.readInt()
        val y = this.readInt()
        val z = this.readInt()
        return Vector3i(x, y, z)
    }

    override fun writeVector3i(x: Int, y: Int, z: Int): LanternByteBuffer {
        this.writeInt(x)
        this.writeInt(y)
        this.writeInt(z)
        return this
    }

    override fun writeVector3i(vector: Vector3i): LanternByteBuffer =
            this.writeVector3i(vector.x, vector.y, vector.z)

    override fun getPosition(index: Int): Vector3i =
            this.readAt(index) { this.readPosition() }

    override fun setPosition(index: Int, vector: Vector3i): LanternByteBuffer =
            this.writeAt(index) { this.writePosition(vector) }

    override fun readPosition(): Vector3i {
        val value = delegate.readLong()
        val x = (value shr 38).toInt()
        val y = (value and 0xfff).toInt()
        val z = (value shl 38 shr 38).toInt() shr 12
        return Vector3i(x, y, z)
    }

    override fun writePosition(x: Int, y: Int, z: Int): LanternByteBuffer {
        this.delegate.writeLong(x.toLong() and 0x3ffffff shl 38 or (z.toLong() and 0x3ffffff shl 12) or (y.toLong() and 0xfff))
        return this
    }

    override fun writePosition(vector: Vector3i): LanternByteBuffer =
            this.writePosition(vector.x, vector.y, vector.z)

    override fun getVector3f(index: Int): Vector3f =
            this.readAt(index) { this.readVector3f() }

    override fun setVector3f(index: Int, vector: Vector3f): LanternByteBuffer =
            this.writeAt(index) { this.writeVector3f(vector) }

    override fun readVector3f(): Vector3f {
        val x = this.delegate.readFloat()
        val y = this.delegate.readFloat()
        val z = this.delegate.readFloat()
        return Vector3f(x, y, z)
    }

    override fun writeVector3f(x: Float, y: Float, z: Float): LanternByteBuffer {
        this.delegate.ensureWritable(Int.SIZE_BYTES * 3)
        this.delegate.writeFloat(x)
        this.delegate.writeFloat(y)
        this.delegate.writeFloat(z)
        return this
    }

    override fun writeVector3f(vector: Vector3f): LanternByteBuffer =
            this.writeVector3f(vector.x, vector.y, vector.z)

    override fun getVector3d(index: Int): Vector3d =
            this.readAt(index) { readVector3d() }

    override fun setVector3d(index: Int, vector: Vector3d): LanternByteBuffer =
            this.writeAt(index) {this.writeVector3d(vector) }

    override fun readVector3d(): Vector3d {
        val x = this.delegate.readDouble()
        val y = this.delegate.readDouble()
        val z = this.delegate.readDouble()
        return Vector3d(x, y, z)
    }

    override fun writeVector3d(x: Double, y: Double, z: Double): LanternByteBuffer {
        this.delegate.ensureWritable(java.lang.Double.BYTES * 3)
        this.delegate.writeDouble(x)
        this.delegate.writeDouble(y)
        this.delegate.writeDouble(z)
        return this
    }

    override fun writeVector3d(vector: Vector3d): LanternByteBuffer =
            this.writeVector3d(vector.x, vector.y, vector.z)

    override fun getNamespacedKey(index: Int): NamespacedKey =
            this.readAt(index) { this.readNamespacedKey() }

    override fun setNamespacedKey(index: Int, namespacedKey: NamespacedKey): LanternByteBuffer =
            this.writeAt(index) { this.writeNamespacedKey(namespacedKey) }

    override fun readNamespacedKey(): NamespacedKey =
            resolveNamespacedKey(this.readString())

    override fun writeNamespacedKey(namespacedKey: NamespacedKey): LanternByteBuffer {
        return if (namespacedKey.namespace == NamespacedKey.MINECRAFT_NAMESPACE) {
            this.writeString(namespacedKey.value)
        } else {
            this.writeString(namespacedKey.toString())
        }
    }

    override fun getRawItemStack(index: Int): RawItemStack? {
        return this.readAt(index) { this.readRawItemStack() }
    }

    override fun setRawItemStack(index: Int, rawItemStack: RawItemStack?): LanternByteBuffer =
            this.writeAt(index) { this.writeRawItemStack(rawItemStack) }

    override fun readRawItemStack(): RawItemStack? {
        return NetworkItemHelper.readRawFrom(this)
    }

    override fun writeRawItemStack(rawItemStack: RawItemStack?): LanternByteBuffer {
        NetworkItemHelper.writeRawTo(this, rawItemStack)
        return this
    }

    private inline fun <R> readAt(index: Int, fn: () -> R): R {
        val oldIndex = this.delegate.readerIndex()
        this.delegate.readerIndex(index)
        val data = fn()
        this.delegate.readerIndex(oldIndex)
        return data
    }

    private inline fun writeAt(index: Int, fn: () -> Unit): LanternByteBuffer {
        val oldIndex = delegate.writerIndex()
        this.delegate.writerIndex(index)
        fn()
        this.delegate.writerIndex(oldIndex)
        return this
    }

    companion object {

        private val UTF_8 = StandardCharsets.UTF_8

        /**
         * Writes a optional int to the [ByteBuf].
         *
         * @param byteBuf The byte buffer
         * @param optValue The optional int value
         */
        fun writeOptVarInt(byteBuf: ByteBuf, optValue: OptionalInt) {
            var value: Long = 0
            if (optValue.isPresent) {
                value = (optValue.asInt shl 1 or 1.toLong().toInt()).toLong()
            }
            writeVarLong(byteBuf, value)
        }

        /**
         * Reads a optional int from the [ByteBuf].
         *
         * @param byteBuf The byte buffer
         * @return The optional int value
         */
        fun readOptVarInt(byteBuf: ByteBuf): OptionalInt {
            // We have a max of 35 bits, so we have 3 extra we can use,
            // one is now used to represent "empty".
            val value = readVarLong(byteBuf, 35)
            return if (value and 0x1 != 0L) {
                OptionalInt.of((value ushr 1).toInt())
            } else OptionalInt.empty()
        }

        fun writeVarInt(buf: ByteBuf, int: Int) {
            var value = int
            while ((value.toLong() and 0xFFFFFF80L) != 0L) {
                buf.writeByte((value and 0x7F) or 0x80)
                value = value ushr 7
            }
            buf.writeByte(value and 0x7F)
        }

        fun readVarInt(buf: ByteBuf): Int {
            var value = 0
            var i = 0
            var b: Int
            while (true) {
                b = buf.readByte().toInt()
                if ((b and 0x80) == 0)
                    break
                value = value or ((b and 0x7F) shl i)
                i += 7
                if (i > 35)
                    throw DecoderException("Variable length is too long!")
            }
            return value or (b shl i)
        }

        /**
         * Reads a long value from the given [ByteBuf] in the varlong encoding. The
         * amount of read bits may not exceed the given limit.
         *
         * @param buf The byte buf
         * @param maxBits The maximum amount of bits
         * @return The read long value
         */
        @JvmOverloads
        fun readVarLong(buf: ByteBuf, maxBits: Int = 63): Long {
            var value = 0L
            var i = 0
            var b: Long
            while (true) {
                b = buf.readByte().toLong()
                if ((b and 0x80L) == 0L)
                    break
                value = value or ((b and 0x7F) shl i)
                i += 7
                if (i > maxBits)
                    throw DecoderException("Variable length is too long!")
            }
            return value or (b shl i)
        }

        fun writeVarLong(buf: ByteBuf, long: Long) {
            var value = long
            while ((value and 0xFFFFFF80L) != 0L) {
                buf.writeByte((value and 0x7F).toInt() or 0x80)
                value = value ushr 7
            }
            buf.writeByte(value.toInt() and 0x7F)
        }
    }
}

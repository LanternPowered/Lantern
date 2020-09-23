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
package org.lanternpowered.server.network.entity.parameter

import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.CodecContext

/**
 * A mutable [ParameterList].
 */
class MutableParameterList : ParameterList() {

    private val entries = HashMap<ParameterType<Any?>, Any?>()

    override val isEmpty: Boolean
        get() = this.entries.isEmpty()

    override fun <T> add(type: ParameterType<T>, value: T) {
        this.entries[type.uncheckedCast()] = value
    }

    override fun write(ctx: CodecContext, buf: ByteBuffer) {
        for ((type, value) in this.entries) {
            buf.writeByte(type.index.toByte())
            buf.writeByte(type.valueType.internalId)
            type.valueType.writer.write(ctx, buf, value)
        }
        super.write(ctx, buf)
    }
}

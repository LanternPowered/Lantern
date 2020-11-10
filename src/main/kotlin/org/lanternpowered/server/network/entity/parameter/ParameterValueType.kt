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

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.value.ContextualValueWriter
import java.util.concurrent.atomic.AtomicInteger

internal inline fun <T> parameterValueType(crossinline write: (buf: ByteBuffer, value: T) -> Unit): ParameterValueType<T> {
    val writer = object : ContextualValueWriter<T> {
        override fun write(ctx: CodecContext, buf: ByteBuffer, value: T) = write(buf, value)
    }
    return ParameterValueType(writer)
}

internal inline fun <T> parameterValueType(crossinline write: (ctx: CodecContext, buf: ByteBuffer, value: T) -> Unit): ParameterValueType<T> {
    val writer = object : ContextualValueWriter<T> {
        override fun write(ctx: CodecContext, buf: ByteBuffer, value: T) = write(ctx, buf, value)
    }
    return ParameterValueType(writer)
}

/**
 * @property internalId The internal id of the parameter value type.
 * @property writer The serializer to encode the values.
 */
class ParameterValueType<T> internal constructor(
        val writer: ContextualValueWriter<T>
) {

    companion object {
        private val counter = AtomicInteger()
    }

    val internalId: Byte = counter.getAndIncrement().toByte()
}

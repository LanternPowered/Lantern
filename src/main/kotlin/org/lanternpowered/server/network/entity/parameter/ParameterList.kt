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

/**
 * Represents a list of parameters of entities.
 */
abstract class ParameterList {

    /**
     * Gets whether this parameter list is empty.
     */
    abstract val isEmpty: Boolean

    /**
     * Adds a value for the given parameter type to this parameter list.
     */
    abstract fun <T> add(type: ParameterType<T>, value: T)

    /**
     * Writes this [ParameterList] to the [ByteBuffer].
     *
     * @param buf The byte buffer
     */
    protected open fun write(ctx: CodecContext, buf: ByteBuffer) {
        buf.writeByte(0xff.toByte())
    }

    companion object : ContextualValueWriter<ParameterList> {
        override fun write(ctx: CodecContext, buf: ByteBuffer, value: ParameterList) = value.write(ctx, buf)
    }
}

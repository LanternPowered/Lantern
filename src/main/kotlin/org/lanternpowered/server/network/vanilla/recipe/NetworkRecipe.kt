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
package org.lanternpowered.server.network.vanilla.recipe

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.packet.codec.CodecContext

abstract class NetworkRecipe(val id: String, val type: String) {

    companion object : ContextualValueCodec<NetworkRecipe> {

        override fun write(ctx: CodecContext, buf: ByteBuffer, value: NetworkRecipe) {
            buf.writeString(value.id)
            buf.writeString(value.type)
            value.writeProperties(ctx, buf)
        }

        override fun read(ctx: CodecContext, buf: ByteBuffer): NetworkRecipe = throw UnsupportedOperationException()
    }

    protected abstract fun writeProperties(ctx: CodecContext, buf: ByteBuffer)
}

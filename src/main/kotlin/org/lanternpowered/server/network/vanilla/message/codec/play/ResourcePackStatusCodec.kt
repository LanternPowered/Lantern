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
package org.lanternpowered.server.network.vanilla.message.codec.play

import io.netty.handler.codec.DecoderException
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.ResourcePackStatusMessage
import org.spongepowered.api.event.entity.living.player.ResourcePackStatusEvent.ResourcePackStatus

class ResourcePackStatusCodec : Codec<ResourcePackStatusMessage> {

    private val status = Int2ObjectOpenHashMap<ResourcePackStatus>()

    override fun decode(context: CodecContext, buf: ByteBuffer): ResourcePackStatusMessage {
        val statusCode = buf.readVarInt()
        val status = this.status[statusCode] ?: throw DecoderException("Unknown status: $statusCode")
        return ResourcePackStatusMessage(status)
    }

    init {
        this.status[0] = ResourcePackStatus.SUCCESSFULLY_LOADED
        this.status[1] = ResourcePackStatus.DECLINED
        this.status[2] = ResourcePackStatus.FAILED
        this.status[3] = ResourcePackStatus.ACCEPTED
    }
}

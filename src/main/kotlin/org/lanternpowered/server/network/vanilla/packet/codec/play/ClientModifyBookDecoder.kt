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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import io.netty.handler.codec.CodecException
import io.netty.handler.codec.DecoderException
import org.lanternpowered.server.data.io.store.item.WritableBookItemTypeObjectSerializer
import org.lanternpowered.server.data.io.store.item.WrittenBookItemTypeObjectSerializer
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifyBookPacket
import org.spongepowered.api.data.type.HandTypes

object ClientModifyBookDecoder : PacketDecoder<ClientModifyBookPacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientModifyBookPacket {
        val rawItemStack = buf.readRawItemStack()
        val sign = buf.readBoolean()
        val handType = if (buf.readVarInt() == 0) HandTypes.MAIN_HAND.get() else HandTypes.OFF_HAND.get()
        if (rawItemStack == null)
            throw DecoderException("Modified book may not be null!")
        val dataView = rawItemStack.dataView ?: throw DecoderException("Modified book data view (nbt tag) may not be null!")
        val pages = dataView.getStringList(WritableBookItemTypeObjectSerializer.PAGES)
                .orElseThrow { DecoderException("Edited book pages missing!") }
        if (sign) {
            val author = dataView.getString(WrittenBookItemTypeObjectSerializer.AUTHOR)
                    .orElseThrow { CodecException("Signed book author missing!") }
            val title = dataView.getString(WrittenBookItemTypeObjectSerializer.TITLE)
                    .orElseThrow { CodecException("Signed book title missing!") }
            return ClientModifyBookPacket.Sign(handType, pages, author, title)
        }
        return ClientModifyBookPacket.Edit(handType, pages)
    }
}

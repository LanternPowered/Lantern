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

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTradeOffers

class CodecPlayOutTradeOffers : Codec<MessagePlayOutTradeOffers> {

    override fun encode(context: CodecContext, message: MessagePlayOutTradeOffers): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(message.windowId)
            val tradeOffers = message.tradeOffers
            writeByte(tradeOffers.size.toByte())
            tradeOffers.forEach {
                context.write(this, ContextualValueTypes.TRADE_OFFER, it)
            }
            writeVarInt(message.villagerLevel)
            writeVarInt(message.experience)
            writeBoolean(message.regularVillager)
            writeBoolean(message.canRestock)
        }
    }
}

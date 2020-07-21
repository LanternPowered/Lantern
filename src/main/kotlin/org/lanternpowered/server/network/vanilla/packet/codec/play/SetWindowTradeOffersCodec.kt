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

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowTradeOffersPacket

class SetWindowTradeOffersCodec : Codec<SetWindowTradeOffersPacket> {

    override fun encode(context: CodecContext, packet: SetWindowTradeOffersPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(packet.windowId)
            val tradeOffers = packet.tradeOffers
            writeByte(tradeOffers.size.toByte())
            tradeOffers.forEach {
                context.write(this, ContextualValueTypes.TRADE_OFFER, it)
            }
            writeVarInt(packet.villagerLevel)
            writeVarInt(packet.experience)
            writeBoolean(packet.regularVillager)
            writeBoolean(packet.canRestock)
        }
    }
}

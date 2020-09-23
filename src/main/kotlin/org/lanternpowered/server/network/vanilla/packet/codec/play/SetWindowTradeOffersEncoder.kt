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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowTradeOffersPacket
import org.lanternpowered.server.network.vanilla.trade.NetworkTradeOffer

object SetWindowTradeOffersEncoder : PacketEncoder<SetWindowTradeOffersPacket> {

    override fun encode(ctx: CodecContext, packet: SetWindowTradeOffersPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.windowId)
        val tradeOffers = packet.tradeOffers
        buf.writeByte(tradeOffers.size.toByte())
        for (tradeOffer in tradeOffers)
            NetworkTradeOffer.write(ctx, buf, tradeOffer)
        buf.writeVarInt(packet.villagerLevel)
        buf.writeVarInt(packet.experience)
        buf.writeBoolean(packet.regularVillager)
        buf.writeBoolean(packet.canRestock)
        return buf
    }
}

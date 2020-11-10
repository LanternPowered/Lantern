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
import io.netty.util.AttributeKey
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.UnknownPacket
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientLeaveBedPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRequestHorseInventoryPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSneakStatePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSprintStatePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientStartElytraFlyingPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientVehicleJumpPacket

object ClientPlayerActionDecoder : PacketDecoder<Packet> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): Packet {
        // Normally should this be the entity id, but only the
        // client player will send this, so it won't be used
        buf.readVarInt()
        val action = buf.readVarInt()
        val value = buf.readVarInt()
        // Sneaking states
        if (action == 0 || action == 1) {
            return ClientSneakStatePacket(action == 0)
            // Sprinting states
        } else if (action == 3 || action == 4) {
            return ClientSprintStatePacket(action == 3)
            // Leave bed button is pressed
        } else if (action == 2) {
            return ClientLeaveBedPacket
            // Horse jump start
        } else if (action == 5) {
            return UnknownPacket
            // Horse jump stop
        } else if (action == 6) {
            // Make sure that the vehicle movement message doesn't add the jump message as well
            ctx.session.attr(CANCEL_NEXT_JUMP_MESSAGE).set(true)
            return ClientVehicleJumpPacket(false, value.toFloat() / 100f)
        } else if (action == 7) {
            return ClientRequestHorseInventoryPacket
        } else if (action == 8) {
            return ClientStartElytraFlyingPacket
        }
        throw CodecException("Unknown action type: $action")
    }

    @JvmField
    val CANCEL_NEXT_JUMP_MESSAGE: AttributeKey<Boolean> = AttributeKey.valueOf<Boolean>("cancel-next-jump-message")
}

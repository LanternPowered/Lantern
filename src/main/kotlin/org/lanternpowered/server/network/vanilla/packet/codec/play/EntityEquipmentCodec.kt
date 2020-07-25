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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityEquipmentPacket

object EntityEquipmentCodec : PacketEncoder<EntityEquipmentPacket> {

    override fun encode(context: CodecContext, packet: EntityEquipmentPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        val itr = packet.entries.int2ObjectEntrySet().iterator()
        while (itr.hasNext()) {
            val (slot, itemStack) = itr.next()
            var slotValue = slot
            if (itr.hasNext())
                slotValue += 0x80
            buf.writeByte(slotValue.toByte())
            context.write(buf, ContextualValueTypes.ITEM_STACK, itemStack)
        }
        return buf
    }
}

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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.text.LegacyNetworkText
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.TeamPacket
import org.lanternpowered.server.registry.type.scoreboard.CollisionRuleRegistry
import org.lanternpowered.server.registry.type.scoreboard.VisibilityRegistry

object TeamEncoder : PacketEncoder<TeamPacket> {

    private val colorIndex = NamedTextColor.values()
            .withIndex().associateTo(Object2IntOpenHashMap()) { (index, value) -> value to index }

    override fun encode(ctx: CodecContext, packet: TeamPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeString(packet.teamName)
        val type = when (packet) {
            is TeamPacket.Create -> 0
            is TeamPacket.Remove -> 1
            is TeamPacket.Update -> 2
            is TeamPacket.AddMembers -> 3
            is TeamPacket.RemoveMembers -> 4
            else -> error("Unknown type: ${packet.javaClass.name}")
        }
        buf.writeByte(type.toByte())
        if (packet is TeamPacket.CreateOrUpdate) {
            NetworkText.write(ctx, buf, packet.displayName)
            var flags = 0
            if (packet.friendlyFire)
                flags += 0x1
            if (packet.seeFriendlyInvisibles)
                flags += 0x2
            buf.writeByte(flags.toByte())
            buf.writeString(VisibilityRegistry.requireId(packet.nameTagVisibility))
            buf.writeString(CollisionRuleRegistry.requireId(packet.collisionRule))
            buf.writeByte(if (packet.color == null) 21 else this.colorIndex.getInt(packet.color).toByte())
            NetworkText.write(ctx, buf, packet.prefix)
            NetworkText.write(ctx, buf, packet.suffix)
        }
        if (packet is TeamPacket.Members) {
            buf.writeVarInt(packet.members.size)
            for (member in packet.members)
                LegacyNetworkText.write(ctx, buf, member)
        }
        return buf
    }
}

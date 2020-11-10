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

import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket
import org.lanternpowered.server.registry.type.data.GameModeRegistry

object TabListEncoder : PacketEncoder<TabListPacket> {

    override fun encode(ctx: CodecContext, packet: TabListPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        val entries = packet.entries

        val type = when (entries.first()) {
            is TabListPacket.Entry.Add -> 0
            is TabListPacket.Entry.UpdateGameMode -> 1
            is TabListPacket.Entry.UpdateLatency -> 2
            is TabListPacket.Entry.UpdateDisplayName -> 3
            is TabListPacket.Entry.Remove -> 4
        }

        buf.writeVarInt(type)
        buf.writeVarInt(entries.size)

        for (entry in entries) {
            buf.writeUniqueId(entry.gameProfile.uniqueId)

            when (entry) {
                is TabListPacket.Entry.Add -> {
                    buf.writeString(entry.gameProfile.name.orElse("unknown"))
                    val properties = entry.gameProfile.propertyMap.values()
                    buf.writeVarInt(properties.size)
                    for (property in properties) {
                        buf.writeString(property.name)
                        buf.writeString(property.value)
                        val signature = property.signature.orNull()
                        buf.writeBoolean(signature != null)
                        if (signature != null)
                            buf.writeString(signature)
                    }
                    buf.writeVarInt(GameModeRegistry.getId(entry.gameMode))
                    buf.writeVarInt(entry.ping)
                    val displayName = entry.displayName
                    buf.writeBoolean(displayName != null)
                    if (displayName != null)
                        NetworkText.write(ctx, buf, displayName)
                }
                is TabListPacket.Entry.UpdateGameMode -> {
                    buf.writeVarInt(GameModeRegistry.getId(entry.gameMode))
                }
                is TabListPacket.Entry.UpdateLatency -> {
                    buf.writeVarInt(entry.ping)
                }
                is TabListPacket.Entry.UpdateDisplayName -> {
                    val displayName = entry.displayName
                    buf.writeBoolean(displayName != null)
                    if (displayName != null)
                        NetworkText.write(ctx, buf, displayName)
                }
                is TabListPacket.Entry.Remove -> {}
            }
        }
        return buf
    }
}

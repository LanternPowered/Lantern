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

import io.netty.handler.codec.EncoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar.UpdateMisc
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar.UpdatePercent
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar.UpdateStyle
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBossBar.UpdateTitle
import org.lanternpowered.server.registry.type.boss.BossBarColorRegistry
import org.lanternpowered.server.registry.type.boss.BossBarOverlayRegistry

class CodecPlayOutBossBar : Codec<MessagePlayOutBossBar> {

    override fun encode(context: CodecContext, message: MessagePlayOutBossBar): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeUniqueId(message.uniqueId)
            when (message) {
                is MessagePlayOutBossBar.Add -> {
                    writeVarInt(0)
                    context.write(this, ContextualValueTypes.TEXT, message.title)
                    writeFloat(message.health)
                    writeVarInt(BossBarColorRegistry.getId(message.color))
                    writeVarInt(BossBarOverlayRegistry.getId(message.overlay))
                    writeByte(toFlags(message.isDarkenSky, message.isEndMusic, message.shouldCreateFog()).toByte())
                }
                is MessagePlayOutBossBar.Remove -> {
                    writeVarInt(1)
                }
                is UpdatePercent -> {
                    writeVarInt(2)
                    writeFloat(message.percent)
                }
                is UpdateTitle -> {
                    writeVarInt(3)
                    context.write(this, ContextualValueTypes.TEXT, message.title)
                }
                is UpdateStyle -> {
                    writeVarInt(4)
                    writeVarInt(BossBarColorRegistry.getId(message.color))
                    writeVarInt(BossBarOverlayRegistry.getId(message.overlay))
                }
                is UpdateMisc -> {
                    writeVarInt(5)
                    writeByte(toFlags(message.isDarkenSky, message.isEndMusic, message.shouldCreateFog()).toByte())
                }
                else -> throw EncoderException("Unsupported message type: " + message.javaClass.name)
            }
        }
    }

    private fun toFlags(darkenSky: Boolean, endMusic: Boolean, createFog: Boolean): Int {
        var flags = 0
        if (darkenSky)
            flags += 0x1
        if (endMusic)
            flags += 0x2
        if (createFog)
            flags += 0x4
        return flags
    }
}

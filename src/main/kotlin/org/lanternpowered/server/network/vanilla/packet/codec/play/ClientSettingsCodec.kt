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
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSettingsPacket
import org.lanternpowered.server.registry.type.text.ChatVisibilityRegistry
import org.lanternpowered.server.util.LocaleCache
import org.spongepowered.api.data.type.HandPreferences

class ClientSettingsCodec : Codec<ClientSettingsPacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ClientSettingsPacket {
        val locale = LocaleCache[buf.readLimitedString(16)]
        val viewDistance = buf.readByte().toInt()
        val visibility = ChatVisibilityRegistry.require(buf.readByte().toInt())
        val enableColors = buf.readBoolean()
        val skinPartsBitPattern = buf.readByte().toInt() and 0xff
        val dominantHand = if (buf.readVarInt() == 1) HandPreferences.RIGHT.get() else HandPreferences.LEFT.get()
        return ClientSettingsPacket(locale, viewDistance, visibility, dominantHand, enableColors, skinPartsBitPattern)
    }
}

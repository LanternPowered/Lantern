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
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientAcceptBeaconEffectsPacket
import org.lanternpowered.server.registry.type.potion.PotionEffectTypeRegistry

object ClientAcceptBeaconEffectsCodec : PacketDecoder<ClientAcceptBeaconEffectsPacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ClientAcceptBeaconEffectsPacket {
        val primary = PotionEffectTypeRegistry.get(buf.readVarInt())
        val secondary = PotionEffectTypeRegistry.get(buf.readVarInt())
        return ClientAcceptBeaconEffectsPacket(primary, secondary)
    }
}

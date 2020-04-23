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
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInAcceptBeaconEffects
import org.lanternpowered.server.registry.type.potion.PotionEffectTypeRegistry

class CodecPlayInAcceptBeaconEffects : Codec<MessagePlayInAcceptBeaconEffects> {

    override fun decode(context: CodecContext, buf: ByteBuffer): MessagePlayInAcceptBeaconEffects {
        val primary = PotionEffectTypeRegistry.get(buf.readVarInt())
        val secondary = PotionEffectTypeRegistry.get(buf.readVarInt())
        return MessagePlayInAcceptBeaconEffects(primary, secondary)
    }
}

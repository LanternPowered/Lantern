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
import org.lanternpowered.server.network.item.NetworkItemHelper
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.SetCooldownMessage

class SetCooldownCodec : Codec<SetCooldownMessage> {

    override fun encode(context: CodecContext, message: SetCooldownMessage): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        NetworkItemHelper.writeTypeTo(buf, message.itemType)
        buf.writeVarInt(message.cooldownTicks)
        return buf
    }
}

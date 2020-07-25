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
import org.lanternpowered.server.network.vanilla.packet.type.play.SetRecipesPacket

object SetRecipesCodec : PacketEncoder<SetRecipesPacket> {

    override fun encode(context: CodecContext, packet: SetRecipesPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        val recipes = packet.recipes
        buf.writeVarInt(recipes.size)
        for (recipe in recipes)
            context.write(buf, ContextualValueTypes.RECIPE, recipe)
        return buf
    }
}

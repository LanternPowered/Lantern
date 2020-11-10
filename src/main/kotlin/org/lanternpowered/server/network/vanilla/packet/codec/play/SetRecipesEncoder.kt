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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetRecipesPacket
import org.lanternpowered.server.network.vanilla.recipe.NetworkRecipe

object SetRecipesEncoder : PacketEncoder<SetRecipesPacket> {

    override fun encode(ctx: CodecContext, packet: SetRecipesPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        val recipes = packet.recipes
        buf.writeVarInt(recipes.size)
        for (recipe in recipes)
            NetworkRecipe.write(ctx, buf, recipe)
        return buf
    }
}

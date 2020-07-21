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

import org.lanternpowered.server.item.recipe.RecipeBookState
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.Packet
import org.lanternpowered.server.network.message.UnknownPacket
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDisplayedRecipe
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRecipeBookStatesPacket

class ClientRecipeBookCodec : Codec<Packet> {

    override fun decode(context: CodecContext, buf: ByteBuffer): Packet {
        val type = buf.readVarInt()
        if (type == 0) {
            val id = buf.readString()
            return PacketPlayInDisplayedRecipe(id)
        } else if (type == 1) {
            fun readBookState(): RecipeBookState {
                val open = buf.readBoolean()
                val filter = buf.readBoolean()
                return RecipeBookState(open, filter)
            }
            val crafting = readBookState()
            val smelting = readBookState()
            val unknown1 = readBookState()
            val unknown2 = readBookState()
            return ClientRecipeBookStatesPacket(crafting, smelting)
        }
        return UnknownPacket
    }
}

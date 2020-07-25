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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.UnlockRecipesPacket

object UnlockRecipesCodec : PacketEncoder<UnlockRecipesPacket> {

    private fun ByteBuffer.writeBookState(bookState: RecipeBookState) {
        writeBoolean(bookState.isCurrentlyOpen)
        writeBoolean(bookState.isFilterActive)
    }

    override fun encode(context: CodecContext, packet: UnlockRecipesPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        val action = when (packet) {
            is UnlockRecipesPacket.Initialize -> 0
            is UnlockRecipesPacket.Add -> 1
            is UnlockRecipesPacket.Remove -> 2
        }
        buf.writeVarInt(action)
        val bookStates = packet.recipeBookStates
        buf.writeBookState(bookStates.crafting)
        buf.writeBookState(bookStates.smelting)
        buf.writeBookState(bookStates.blastFurnace)
        buf.writeBookState(bookStates.smoker)
        var recipeIds = packet.recipeIds
        buf.writeVarInt(recipeIds.size)
        for (recipeId in recipeIds)
            buf.writeString(recipeId)
        if (packet is UnlockRecipesPacket.Initialize) {
            recipeIds = packet.recipeIdsToBeDisplayed
            buf.writeVarInt(recipeIds.size)
            for (recipeId in recipeIds)
                buf.writeString(recipeId)
        }
        return buf
    }
}

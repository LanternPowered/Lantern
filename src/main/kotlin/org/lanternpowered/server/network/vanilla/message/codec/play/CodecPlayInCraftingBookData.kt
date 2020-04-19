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

import org.lanternpowered.server.item.recipe.RecipeBookState
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.message.UnknownMessage
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDisplayedRecipe
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRecipeBookStates

class CodecPlayInCraftingBookData : Codec<Message> {

    override fun decode(context: CodecContext, buf: ByteBuffer): Message {
        val type = buf.readVarInt()
        if (type == 0) {
            val id = buf.readString()
            return MessagePlayInDisplayedRecipe(id)
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
            return MessagePlayInRecipeBookStates(crafting, smelting)
        }
        return UnknownMessage
    }
}

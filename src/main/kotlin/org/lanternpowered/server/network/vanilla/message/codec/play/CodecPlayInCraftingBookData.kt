/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

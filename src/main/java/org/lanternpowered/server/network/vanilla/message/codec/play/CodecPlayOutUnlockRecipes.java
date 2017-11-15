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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.item.recipe.RecipeBookState;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnlockRecipes;

import java.util.List;

public final class CodecPlayOutUnlockRecipes implements Codec<MessagePlayOutUnlockRecipes> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutUnlockRecipes message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        if (message instanceof MessagePlayOutUnlockRecipes.Remove) {
            buf.writeVarInt((short) 2);
        } else if (message instanceof MessagePlayOutUnlockRecipes.Add) {
            buf.writeVarInt((short) 1);
        } else if (message instanceof MessagePlayOutUnlockRecipes.Init) {
            buf.writeVarInt((short) 0);
        } else {
            throw new EncoderException();
        }
        RecipeBookState bookState = message.getCraftingRecipeBookState();
        buf.writeBoolean(bookState.isCurrentlyOpen());
        buf.writeBoolean(bookState.isFilterActive());
        bookState = message.getSmeltingRecipeBookState();
        buf.writeBoolean(bookState.isCurrentlyOpen());
        buf.writeBoolean(bookState.isFilterActive());
        List<String> recipeIds = message.getRecipeIds();
        buf.writeVarInt(recipeIds.size());
        recipeIds.forEach(buf::writeString);
        if (message instanceof MessagePlayOutUnlockRecipes.Init) {
            recipeIds = ((MessagePlayOutUnlockRecipes.Init) message).getRecipeIdsToBeDisplayed();
            buf.writeVarInt(recipeIds.size());
            recipeIds.forEach(buf::writeString);
        }
        return buf;
    }
}

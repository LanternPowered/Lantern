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

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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.item.recipe.RecipeBookState;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutUnlockRecipes;

import java.util.List;

public final class CodecPlayOutUnlockRecipes implements Codec<PacketPlayOutUnlockRecipes> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutUnlockRecipes packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        if (packet instanceof PacketPlayOutUnlockRecipes.Remove) {
            buf.writeVarInt((short) 2);
        } else if (packet instanceof PacketPlayOutUnlockRecipes.Add) {
            buf.writeVarInt((short) 1);
        } else if (packet instanceof PacketPlayOutUnlockRecipes.Init) {
            buf.writeVarInt((short) 0);
        } else {
            throw new EncoderException();
        }
        RecipeBookState bookState = packet.getCraftingRecipeBookState();
        buf.writeBoolean(bookState.isCurrentlyOpen());
        buf.writeBoolean(bookState.isFilterActive());
        bookState = packet.getSmeltingRecipeBookState();
        buf.writeBoolean(bookState.isCurrentlyOpen());
        buf.writeBoolean(bookState.isFilterActive());
        List<String> recipeIds = packet.getRecipeIds();
        buf.writeVarInt(recipeIds.size());
        recipeIds.forEach(buf::writeString);
        if (packet instanceof PacketPlayOutUnlockRecipes.Init) {
            recipeIds = ((PacketPlayOutUnlockRecipes.Init) packet).getRecipeIdsToBeDisplayed();
            buf.writeVarInt(recipeIds.size());
            recipeIds.forEach(buf::writeString);
        }
        return buf;
    }
}

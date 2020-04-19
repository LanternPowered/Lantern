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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutDefineRecipes;
import org.lanternpowered.server.network.vanilla.recipe.NetworkRecipe;

import java.util.List;

public final class CodecPlayOutDefineRecipes implements Codec<MessagePlayOutDefineRecipes> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutDefineRecipes message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final List<NetworkRecipe> recipes = message.getRecipes();
        buf.writeVarInt(recipes.size());
        for (NetworkRecipe recipe : recipes) {
            context.write(buf, ContextualValueTypes.RECIPE, recipe);
        }
        return buf;
    }
}

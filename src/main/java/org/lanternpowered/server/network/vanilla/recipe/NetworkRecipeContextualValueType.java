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
package org.lanternpowered.server.network.vanilla.recipe;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueType;
import org.lanternpowered.server.network.message.codec.CodecContext;

public final class NetworkRecipeContextualValueType implements ContextualValueType<NetworkRecipe> {

    @Override
    public void write(CodecContext ctx, NetworkRecipe recipe, ByteBuffer buf) throws CodecException {
        buf.writeString(recipe.getId());
        buf.writeString(recipe.getType());
        recipe.write(ctx, buf);
    }

    @Override
    public NetworkRecipe read(CodecContext ctx, ByteBuffer buf) throws CodecException {
        throw new UnsupportedOperationException();
    }
}

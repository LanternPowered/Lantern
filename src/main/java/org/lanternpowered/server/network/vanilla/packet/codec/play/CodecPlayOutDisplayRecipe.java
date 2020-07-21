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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDisplayRecipe;

public final class CodecPlayOutDisplayRecipe implements Codec<PacketPlayOutDisplayRecipe> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutDisplayRecipe packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeByte((byte) packet.getWindowId());
        buf.writeString(packet.getRecipeId());
        return buf;
    }
}

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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockBreakAnimation;

public final class CodecPlayOutBlockBreakAnimation implements Codec<PacketPlayOutBlockBreakAnimation> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutBlockBreakAnimation message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getId());
        buf.writePosition(message.getPosition());
        // Make sure that the state fits in the byte
        int state = message.getState();
        buf.writeByte((byte) (state >= 0 && state <= 9 ? state : 10));
        return buf;
    }
}

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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockAction;

public final class CodecPlayOutBlockAction implements Codec<PacketPlayOutBlockAction> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutBlockAction packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writePosition(packet.getPosition());
        final int[] parameters = packet.getParameters();
        buf.writeByte((byte) parameters[0]);
        buf.writeByte((byte) parameters[1]);
        buf.writeVarInt(packet.getBlockType());
        return buf;
    }
}

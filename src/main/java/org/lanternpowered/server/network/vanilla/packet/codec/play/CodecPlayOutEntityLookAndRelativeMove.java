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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityLookAndRelativeMove;

public final class CodecPlayOutEntityLookAndRelativeMove implements Codec<PacketPlayOutEntityLookAndRelativeMove> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutEntityLookAndRelativeMove packet) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(packet.getEntityId());
        buf.writeShort((short) packet.getDeltaX());
        buf.writeShort((short) packet.getDeltaY());
        buf.writeShort((short) packet.getDeltaZ());
        buf.writeByte(packet.getYaw());
        buf.writeByte(packet.getPitch());
        buf.writeBoolean(packet.isOnGround());
        return buf;
    }
}

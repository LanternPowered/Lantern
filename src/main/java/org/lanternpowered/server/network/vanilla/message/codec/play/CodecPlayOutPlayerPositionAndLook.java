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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerPositionAndLook;
import org.spongepowered.api.util.RelativePositions;

import java.util.Set;

public final class CodecPlayOutPlayerPositionAndLook implements Codec<MessagePlayOutPlayerPositionAndLook> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutPlayerPositionAndLook message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVector3d(message.getPosition());
        buf.writeFloat(message.getYaw());
        buf.writeFloat(message.getPitch());
        final Set<RelativePositions> relativePositions = message.getRelativePositions();
        byte flags = 0;
        if (relativePositions.contains(RelativePositions.X)) {
            flags |= 0x01;
        }
        if (relativePositions.contains(RelativePositions.Y)) {
            flags |= 0x02;
        }
        if (relativePositions.contains(RelativePositions.Z)) {
            flags |= 0x04;
        }
        if (relativePositions.contains(RelativePositions.PITCH)) {
            flags |= 0x08;
        }
        if (relativePositions.contains(RelativePositions.YAW)) {
            flags |= 0x10;
        }
        buf.writeByte(flags);
        buf.writeVarInt(message.getTeleportId());
        return buf;
    }
}

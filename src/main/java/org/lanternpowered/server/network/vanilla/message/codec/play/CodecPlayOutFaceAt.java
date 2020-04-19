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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutFaceAt;
import org.spongepowered.math.vector.Vector3d;

public final class CodecPlayOutFaceAt implements Codec<MessagePlayOutFaceAt> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutFaceAt message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getSourceBodyPosition().ordinal());
        final Vector3d pos = message.getPosition();
        buf.writeDouble(pos.getX());
        buf.writeDouble(pos.getY());
        buf.writeDouble(pos.getZ());
        final boolean flag = message instanceof MessagePlayOutFaceAt.Entity;
        buf.writeBoolean(flag);
        if (flag) {
            final MessagePlayOutFaceAt.Entity message1 = (MessagePlayOutFaceAt.Entity) message;
            buf.writeVarInt(message1.getEntityId());
            buf.writeVarInt(message1.getEntityBodyPosition().ordinal());
        }
        return buf;
    }
}

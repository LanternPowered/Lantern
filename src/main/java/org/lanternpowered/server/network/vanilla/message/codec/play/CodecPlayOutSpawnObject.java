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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnObject;
import org.spongepowered.math.vector.Vector3d;

public final class CodecPlayOutSpawnObject implements Codec<MessagePlayOutSpawnObject> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutSpawnObject message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getEntityId());
        buf.writeUniqueId(message.getUniqueId());
        buf.writeVarInt((byte) message.getObjectType());
        buf.writeVector3d(message.getPosition());
        buf.writeByte((byte) message.getPitch());
        buf.writeByte((byte) message.getYaw());
        buf.writeInteger(message.getObjectData());
        final Vector3d velocity = message.getVelocity();
        buf.writeShort((short) Math.min(velocity.getX() * 8000.0, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(velocity.getY() * 8000.0, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(velocity.getZ() * 8000.0, Short.MAX_VALUE));
        return buf;
    }
}

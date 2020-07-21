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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerMovementAndLook;
import org.spongepowered.math.vector.Vector3d;

public final class CodecPlayInPlayerMovementAndLook implements Codec<PacketPlayInPlayerMovementAndLook> {

    @Override
    public PacketPlayInPlayerMovementAndLook decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final Vector3d position = buf.readVector3d();
        final float yaw = buf.readFloat();
        final float pitch = buf.readFloat();
        final boolean onGround = buf.readBoolean();
        return new PacketPlayInPlayerMovementAndLook(position, yaw, pitch, onGround);
    }
}

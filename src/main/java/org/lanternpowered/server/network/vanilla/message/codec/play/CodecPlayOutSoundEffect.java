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
import org.lanternpowered.server.effect.sound.LanternSoundCategory;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.spongepowered.math.vector.Vector3d;

public final class CodecPlayOutSoundEffect implements Codec<MessagePlayOutSoundEffect> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutSoundEffect message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getType());
        buf.writeVarInt(((LanternSoundCategory) message.getCategory()).getInternalId());
        final Vector3d pos = message.getPosition();
        buf.writeInteger((int) (pos.getX() * 8.0));
        buf.writeInteger((int) (pos.getY() * 8.0));
        buf.writeInteger((int) (pos.getZ() * 8.0));
        buf.writeFloat(message.getVolume());
        buf.writeFloat(message.getPitch());
        return buf;
    }
}

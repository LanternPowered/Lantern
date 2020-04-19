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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntitySoundEffect;

public final class CodecPlayOutEntitySoundEffect implements Codec<MessagePlayOutEntitySoundEffect> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutEntitySoundEffect message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getType());
        buf.writeVarInt(((LanternSoundCategory) message.getCategory()).getInternalId());
        buf.writeVarInt(message.getEntityId());
        buf.writeFloat(message.getVolume());
        buf.writeFloat(message.getPitch());
        return buf;
    }
}

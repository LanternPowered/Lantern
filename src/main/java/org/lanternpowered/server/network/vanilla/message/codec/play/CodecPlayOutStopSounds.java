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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutStopSounds;
import org.spongepowered.api.effect.sound.SoundCategory;

public final class CodecPlayOutStopSounds implements Codec<MessagePlayOutStopSounds> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutStopSounds message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        byte flags = 0;
        final SoundCategory soundCategory = message.getCategory();
        final String sound = message.getSound();
        if (soundCategory != null) {
            flags |= 0x1;
        }
        if (sound != null) {
            flags |= 0x2;
        }
        buf.writeByte(flags);
        if (soundCategory != null) {
            buf.writeVarInt(((LanternSoundCategory) soundCategory).getInternalId());
        }
        if (sound != null) {
            buf.writeString(sound);
        }
        return buf;
    }
}

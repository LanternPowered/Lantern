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
import org.lanternpowered.server.effect.potion.LanternPotionEffectType;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutAddPotionEffect;

public class CodecPlayOutAddPotionEffect implements Codec<PacketPlayOutAddPotionEffect> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutAddPotionEffect message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getEntityId());
        buf.writeByte((byte) ((LanternPotionEffectType) message.getType()).getInternalId());
        buf.writeByte((byte) message.getAmplifier());
        buf.writeVarInt(message.getDuration());
        byte flags = 0;
        if (message.isAmbient()) {
            flags |= 0x1;
        }
        if (message.getShowParticles()) {
            flags |= 0x2;
        }
        buf.writeByte(flags);
        return buf;
    }
}

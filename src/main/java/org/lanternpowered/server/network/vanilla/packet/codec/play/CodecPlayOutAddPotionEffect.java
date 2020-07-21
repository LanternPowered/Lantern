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
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutAddPotionEffect;

public class CodecPlayOutAddPotionEffect implements Codec<PacketPlayOutAddPotionEffect> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutAddPotionEffect packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(packet.getEntityId());
        buf.writeByte((byte) ((LanternPotionEffectType) packet.getType()).getInternalId());
        buf.writeByte((byte) packet.getAmplifier());
        buf.writeVarInt(packet.getDuration());
        byte flags = 0;
        if (packet.isAmbient()) {
            flags |= 0x1;
        }
        if (packet.getShowParticles()) {
            flags |= 0x2;
        }
        buf.writeByte(flags);
        return buf;
    }
}

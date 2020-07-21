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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutPlayerAbilities;

public class CodecPlayOutPlayerAbilities implements Codec<PacketPlayOutPlayerAbilities> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutPlayerAbilities message) throws CodecException {
        byte bits = 0;
        if (message.isInvulnerable()) {
            bits |= 0x1;
        }
        if (message.isFlying()) {
            bits |= 0x2;
        }
        if (message.canFly()) {
            bits |= 0x4;
        }
        if (message.isCreative()) {
            bits |= 0x8;
        }
        final ByteBuffer buf = context.byteBufAlloc().buffer(9);
        buf.writeByte(bits);
        buf.writeFloat(message.getFlySpeed());
        buf.writeFloat(message.getFieldOfView());
        return buf;
    }
}

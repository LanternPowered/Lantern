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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutPlayerHealthUpdate;

public final class CodecPlayOutPlayerHealthUpdate implements Codec<PacketPlayOutPlayerHealthUpdate> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutPlayerHealthUpdate message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeFloat(message.getHealth());
        buf.writeVarInt((int) message.getFood());
        buf.writeFloat(message.getSaturation());
        return buf;
    }
}

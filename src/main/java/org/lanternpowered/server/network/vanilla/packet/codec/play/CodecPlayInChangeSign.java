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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInChangeSign;
import org.spongepowered.math.vector.Vector3i;

public final class CodecPlayInChangeSign implements Codec<PacketPlayInChangeSign> {

    @Override
    public PacketPlayInChangeSign decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final Vector3i position = buf.readPosition();
        final String[] lines = new String[4];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = buf.readLimitedString(384);
        }
        return new PacketPlayInChangeSign(position, lines);
    }
}

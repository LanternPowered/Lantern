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
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInTabComplete;

public final class CodecPlayInTabComplete implements Codec<PacketPlayInTabComplete> {

    @Override
    public PacketPlayInTabComplete decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int unknown = buf.readVarInt();
        final String input = buf.readLimitedString(256);
        return new PacketPlayInTabComplete(input, unknown);
    }
}

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
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPerformRespawn;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInRequestStatistics;

public final class CodecPlayInClientStatus implements Codec<Packet> {

    @Override
    public Packet decode(CodecContext context, ByteBuffer buf) throws CodecException {
        int action = buf.readVarInt();
        switch (action) {
            case 0: return new PacketPlayInPerformRespawn();
            case 1: return new PacketPlayInRequestStatistics();
            default:
                throw new CodecException("Received client status message with unknown action: " + action);
        }
    }
}

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

import static org.lanternpowered.server.network.vanilla.packet.codec.play.CodecUtils.decodeDirection;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Packet;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInDropHeldItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInSwapHandItems;
import org.spongepowered.math.vector.Vector3i;

public final class CodecPlayInPlayerDigging implements Codec<Packet> {

    @Override
    public Packet decode(CodecContext context, ByteBuffer buf) throws CodecException {
        int action = buf.readByte();
        Vector3i position = buf.readPosition();
        int face = buf.readByte();
        switch (action) {
            case 0:
            case 1:
            case 2:
                return new PacketPlayInPlayerDigging(PacketPlayInPlayerDigging.Action.values()[action],
                        position, decodeDirection(face));
            case 3:
            case 4:
                return new PacketPlayInDropHeldItem(action == 3);
            case 5:
                return new PacketPlayInOutFinishUsingItem();
            case 6:
                return new PacketPlayInSwapHandItems();
            default:
                throw new DecoderException("Unknown player digging message action: " + action);
        }
    }
}

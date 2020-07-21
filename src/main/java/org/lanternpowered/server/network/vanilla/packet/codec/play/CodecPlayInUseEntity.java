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
import io.netty.handler.codec.DecoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInUseEntity;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.math.vector.Vector3d;

public final class CodecPlayInUseEntity implements Codec<PacketPlayInUseEntity> {

    @Override
    public PacketPlayInUseEntity decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int entityId = buf.readVarInt();
        final int action = buf.readVarInt();
        if (action == 1) {
            return new PacketPlayInUseEntity.Attack(entityId);
        } else if (action == 0 || action == 2) {
            Vector3d position = null;
            if (action == 2) {
                final double x = buf.readFloat();
                final double y = buf.readFloat();
                final double z = buf.readFloat();
                position = new Vector3d(x, y, z);
            }
            final HandType hand = buf.readVarInt() == 0 ? HandTypes.MAIN_HAND : HandTypes.OFF_HAND;
            return new PacketPlayInUseEntity.Interact(entityId, hand, position);
        } else {
            throw new DecoderException("Received a UseEntity message with a unknown action: " + action);
        }
    }
}

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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetReducedDebugPacket;

public final class CodecPlayOutEntityStatus implements Codec<Packet> {

    private final static int LENGTH = Integer.BYTES + Byte.BYTES;

    @Override
    public ByteBuffer encode(CodecContext context, Packet packet) throws CodecException {
        final int entityId;
        final int action;
        if (packet instanceof SetReducedDebugPacket) {
            entityId = context.getChannel().attr(PlayerJoinCodec.PLAYER_ENTITY_ID).get();
            action = ((SetReducedDebugPacket) packet).isReduced() ? 22 : 23;
        } else if (packet instanceof PacketPlayOutSetOpLevel) {
            entityId = context.getChannel().attr(PlayerJoinCodec.PLAYER_ENTITY_ID).get();
            action = 24 + Math.max(0, Math.min(4, ((PacketPlayOutSetOpLevel) packet).getOpLevel()));
        } else if (packet instanceof PacketPlayOutEntityStatus) {
            entityId = ((PacketPlayOutEntityStatus) packet).getEntityId();
            action = ((PacketPlayOutEntityStatus) packet).getStatus();
        } else if (packet instanceof PacketPlayInOutFinishUsingItem) {
            entityId = context.getChannel().attr(PlayerJoinCodec.PLAYER_ENTITY_ID).get();
            action = 9;
        } else {
            throw new CodecException("Unsupported message type: " + packet.getClass().getName());
        }
        return context.byteBufAlloc().buffer(LENGTH).writeInt(entityId).writeByte((byte) action);
    }
}

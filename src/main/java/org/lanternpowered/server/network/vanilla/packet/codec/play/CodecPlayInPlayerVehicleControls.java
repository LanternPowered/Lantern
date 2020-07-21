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

import static com.google.common.base.MoreObjects.firstNonNull;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.BulkPacket;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerMovementInput;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerVehicleJump;

import java.util.ArrayList;
import java.util.List;

public final class CodecPlayInPlayerVehicleControls implements Codec<Packet> {

    private final static AttributeKey<Boolean> SNEAKING = AttributeKey.valueOf("last-sneaking-state");
    private final static AttributeKey<Boolean> JUMPING = AttributeKey.valueOf("last-jumping-state");

    @Override
    public Packet decode(CodecContext context, ByteBuffer buf) throws CodecException {
        float sideways = buf.readFloat();
        float forwards = buf.readFloat();

        final byte flags = buf.readByte();

        final boolean jump = (flags & 0x1) != 0;
        final boolean sneak = (flags & 0x2) != 0;

        final List<Packet> packets = new ArrayList<>();
        final boolean lastSneak = firstNonNull(context.getChannel().attr(SNEAKING).getAndSet(sneak), false);
        if (lastSneak != sneak) {
            packets.add(new PacketPlayInPlayerSneak(sneak));
        }

        final boolean lastJump = firstNonNull(context.getChannel().attr(JUMPING).getAndSet(jump), false);
        if (lastJump != jump && !firstNonNull(
                context.getChannel().attr(CodecPlayInPlayerAction.CANCEL_NEXT_JUMP_MESSAGE).getAndSet(false), false)) {
            packets.add(new PacketPlayInPlayerVehicleJump(jump, 0f));
        }

        // The mc client already applies the sneak speed, but we want to choose it
        if (sneak) {
            sideways /= 0.3f;
            forwards /= 0.3f;
        }

        packets.add(new PacketPlayInPlayerMovementInput(forwards, sideways));
        return packets.size() == 1 ? packets.get(0) : new BulkPacket(packets);
    }
}

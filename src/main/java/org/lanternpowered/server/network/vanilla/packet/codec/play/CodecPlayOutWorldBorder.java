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
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutWorldBorder;

public final class CodecPlayOutWorldBorder implements Codec<PacketPlayOutWorldBorder> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutWorldBorder packet) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();

        if (packet instanceof PacketPlayOutWorldBorder.Initialize) {
            PacketPlayOutWorldBorder.Initialize message1 = (PacketPlayOutWorldBorder.Initialize) packet;
            buf.writeVarInt(3);
            buf.writeDouble(message1.getCenterX());
            buf.writeDouble(message1.getCenterZ());
            buf.writeDouble(message1.getOldDiameter());
            buf.writeDouble(message1.getNewDiameter());
            buf.writeVarLong(message1.getLerpTime());
            buf.writeVarInt(message1.getWorldSize());
            buf.writeVarInt(message1.getWarningTime());
            buf.writeVarInt(message1.getWarningDistance());
        } else if (packet instanceof PacketPlayOutWorldBorder.UpdateCenter) {
            PacketPlayOutWorldBorder.UpdateCenter message1 = (PacketPlayOutWorldBorder.UpdateCenter) packet;
            buf.writeVarInt(2);
            buf.writeDouble(message1.getX());
            buf.writeDouble(message1.getZ());
        } else if (packet instanceof PacketPlayOutWorldBorder.UpdateLerpedDiameter) {
            PacketPlayOutWorldBorder.UpdateLerpedDiameter message1 = (PacketPlayOutWorldBorder.UpdateLerpedDiameter) packet;
            buf.writeVarInt(1);
            buf.writeDouble(message1.getOldDiameter());
            buf.writeDouble(message1.getNewDiameter());
            buf.writeVarLong(message1.getLerpTime());
        } else if (packet instanceof PacketPlayOutWorldBorder.UpdateDiameter) {
            buf.writeVarInt(0);
            buf.writeDouble(((PacketPlayOutWorldBorder.UpdateDiameter) packet).getDiameter());
        } else if (packet instanceof PacketPlayOutWorldBorder.UpdateWarningDistance) {
            buf.writeVarInt(5);
            buf.writeVarInt(((PacketPlayOutWorldBorder.UpdateWarningDistance) packet).getDistance());
        } else if (packet instanceof PacketPlayOutWorldBorder.UpdateWarningTime) {
            buf.writeVarInt(4);
            buf.writeVarInt(((PacketPlayOutWorldBorder.UpdateWarningTime) packet).getTime());
        } else {
            throw new EncoderException("Unsupported message type: " + packet.getClass().getName());
        }

        return buf;
    }
}

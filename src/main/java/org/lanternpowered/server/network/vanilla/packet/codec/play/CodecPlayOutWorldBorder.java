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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutWorldBorder;

public final class CodecPlayOutWorldBorder implements Codec<PacketPlayOutWorldBorder> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutWorldBorder message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();

        if (message instanceof PacketPlayOutWorldBorder.Initialize) {
            PacketPlayOutWorldBorder.Initialize message1 = (PacketPlayOutWorldBorder.Initialize) message;
            buf.writeVarInt(3);
            buf.writeDouble(message1.getCenterX());
            buf.writeDouble(message1.getCenterZ());
            buf.writeDouble(message1.getOldDiameter());
            buf.writeDouble(message1.getNewDiameter());
            buf.writeVarLong(message1.getLerpTime());
            buf.writeVarInt(message1.getWorldSize());
            buf.writeVarInt(message1.getWarningTime());
            buf.writeVarInt(message1.getWarningDistance());
        } else if (message instanceof PacketPlayOutWorldBorder.UpdateCenter) {
            PacketPlayOutWorldBorder.UpdateCenter message1 = (PacketPlayOutWorldBorder.UpdateCenter) message;
            buf.writeVarInt(2);
            buf.writeDouble(message1.getX());
            buf.writeDouble(message1.getZ());
        } else if (message instanceof PacketPlayOutWorldBorder.UpdateLerpedDiameter) {
            PacketPlayOutWorldBorder.UpdateLerpedDiameter message1 = (PacketPlayOutWorldBorder.UpdateLerpedDiameter) message;
            buf.writeVarInt(1);
            buf.writeDouble(message1.getOldDiameter());
            buf.writeDouble(message1.getNewDiameter());
            buf.writeVarLong(message1.getLerpTime());
        } else if (message instanceof PacketPlayOutWorldBorder.UpdateDiameter) {
            buf.writeVarInt(0);
            buf.writeDouble(((PacketPlayOutWorldBorder.UpdateDiameter) message).getDiameter());
        } else if (message instanceof PacketPlayOutWorldBorder.UpdateWarningDistance) {
            buf.writeVarInt(5);
            buf.writeVarInt(((PacketPlayOutWorldBorder.UpdateWarningDistance) message).getDistance());
        } else if (message instanceof PacketPlayOutWorldBorder.UpdateWarningTime) {
            buf.writeVarInt(4);
            buf.writeVarInt(((PacketPlayOutWorldBorder.UpdateWarningTime) message).getTime());
        } else {
            throw new EncoderException("Unsupported message type: " + message.getClass().getName());
        }

        return buf;
    }
}

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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutConfirmWindowTransaction;

public final class CodecPlayInOutConfirmWindowTransaction implements Codec<PacketPlayInOutConfirmWindowTransaction> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayInOutConfirmWindowTransaction packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer(4);
        buf.writeByte((byte) packet.getWindowId());
        buf.writeShort((short) packet.getTransaction());
        buf.writeBoolean(packet.isAccepted());
        return buf;
    }

    @Override
    public PacketPlayInOutConfirmWindowTransaction decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int windowId = buf.readByte();
        final int transaction = buf.readShort();
        final boolean accepted = buf.readBoolean();
        return new PacketPlayInOutConfirmWindowTransaction(windowId, transaction, accepted);
    }
}

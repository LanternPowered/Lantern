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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutHeldItemChange;

/**
 * Note that incoming and outgoing codecs are not the same, but we use the same
 * message class.
 */
public final class CodecPlayInOutHeldItemChange implements Codec<PacketPlayInOutHeldItemChange> {

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayInOutHeldItemChange message) throws CodecException {
        return context.byteBufAlloc().buffer(1).writeByte((byte) message.getSlot());
    }

    @Override
    public PacketPlayInOutHeldItemChange decode(CodecContext context, ByteBuffer buf) throws CodecException {
        return new PacketPlayInOutHeldItemChange((byte) buf.readShort());
    }
}

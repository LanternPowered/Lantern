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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityCollectItem;

public final class CodecPlayOutEntityCollectItem implements Codec<MessagePlayOutEntityCollectItem> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutEntityCollectItem message) throws CodecException {
        ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getCollectedId());
        buf.writeVarInt(message.getCollectorId());
        buf.writeVarInt(message.getCollectItemCount());
        return buf;
    }
}

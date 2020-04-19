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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInDataRequest;
import org.spongepowered.math.vector.Vector3i;

public final class CodecPlayInDataRequestBlock implements Codec<MessagePlayInDataRequest.Block> {

    @Override
    public MessagePlayInDataRequest.Block decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int transactionId = buf.readVarInt();
        final Vector3i position = buf.readPosition();
        return new MessagePlayInDataRequest.Block(transactionId, position);
    }
}

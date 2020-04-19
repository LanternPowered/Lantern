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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetEntityPassengers;

public final class CodecPlayOutSetEntityPassengers implements Codec<MessagePlayOutSetEntityPassengers> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutSetEntityPassengers message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeVarInt(message.getEntityId());
        final int[] passengersIds = message.getPassengersIds();
        buf.writeVarInt(passengersIds.length);
        for (int entityId : passengersIds) {
            buf.writeVarInt(entityId);
        }
        return buf;
    }
}

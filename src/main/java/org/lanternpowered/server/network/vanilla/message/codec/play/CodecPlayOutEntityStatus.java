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
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;

public final class CodecPlayOutEntityStatus implements Codec<Message> {

    private final static int LENGTH = Integer.BYTES + Byte.BYTES;

    @Override
    public ByteBuffer encode(CodecContext context, Message message) throws CodecException {
        final int entityId;
        final int action;
        if (message instanceof MessagePlayOutSetReducedDebug) {
            entityId = context.getChannel().attr(PlayerJoinCodec.PLAYER_ENTITY_ID).get();
            action = ((MessagePlayOutSetReducedDebug) message).isReduced() ? 22 : 23;
        } else if (message instanceof MessagePlayOutSetOpLevel) {
            entityId = context.getChannel().attr(PlayerJoinCodec.PLAYER_ENTITY_ID).get();
            action = 24 + Math.max(0, Math.min(4, ((MessagePlayOutSetOpLevel) message).getOpLevel()));
        } else if (message instanceof MessagePlayOutEntityStatus) {
            entityId = ((MessagePlayOutEntityStatus) message).getEntityId();
            action = ((MessagePlayOutEntityStatus) message).getStatus();
        } else if (message instanceof MessagePlayInOutFinishUsingItem) {
            entityId = context.getChannel().attr(PlayerJoinCodec.PLAYER_ENTITY_ID).get();
            action = 9;
        } else {
            throw new CodecException("Unsupported message type: " + message.getClass().getName());
        }
        return context.byteBufAlloc().buffer(LENGTH).writeInteger(entityId).writeByte((byte) action);
    }
}

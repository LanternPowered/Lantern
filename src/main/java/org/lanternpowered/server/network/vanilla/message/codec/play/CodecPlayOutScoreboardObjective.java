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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardObjective;
import org.lanternpowered.server.scoreboard.LanternObjectiveDisplayMode;

public final class CodecPlayOutScoreboardObjective implements Codec<MessagePlayOutScoreboardObjective> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutScoreboardObjective message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeString(message.getObjectiveName());
        if (message instanceof MessagePlayOutScoreboardObjective.CreateOrUpdate) {
            buf.writeByte((byte) (message instanceof MessagePlayOutScoreboardObjective.Create ? 0 : 2));
            final MessagePlayOutScoreboardObjective.CreateOrUpdate message0 =
                    (MessagePlayOutScoreboardObjective.CreateOrUpdate) message;
            context.write(buf, ContextualValueTypes.TEXT, message0.getDisplayName());
            buf.writeVarInt(((LanternObjectiveDisplayMode) message0.getDisplayMode()).getInternalId());
        } else {
            buf.writeByte((byte) 1);
        }
        return buf;
    }
}

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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutScoreboardScore;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.translation.TranslationContext;

public final class CodecPlayOutScoreboardScore implements Codec<MessagePlayOutScoreboardScore> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutScoreboardScore message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        try (TranslationContext ignored = TranslationContext.enter()
                .locale(context.getSession().getLocale())
                .enableForcedTranslations()) {
            buf.writeString(LanternTexts.toLegacy(message.getScoreName()));
        }
        final int action = message instanceof MessagePlayOutScoreboardScore.Remove ? 1 : 0;
        buf.writeByte((byte) action);
        buf.writeString(message.getObjectiveName());
        if (action == 0) {
            buf.writeVarInt(((MessagePlayOutScoreboardScore.CreateOrUpdate) message).getValue());
        }
        return buf;
    }
}

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
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardScorePacket;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.translation.TranslationContext;

public final class CodecPlayOutScoreboardScore implements Codec<ScoreboardScorePacket> {

    @Override
    public ByteBuffer encode(CodecContext context, ScoreboardScorePacket packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        try (TranslationContext ignored = TranslationContext.enter()
                .locale(context.getSession().getLocale())
                .enableForcedTranslations()) {
            buf.writeString(LanternTexts.toLegacy(packet.getScoreName()));
        }
        final int action = packet instanceof ScoreboardScorePacket.Remove ? 1 : 0;
        buf.writeByte((byte) action);
        buf.writeString(packet.getObjectiveName());
        if (action == 0) {
            buf.writeVarInt(((ScoreboardScorePacket.CreateOrUpdate) packet).getValue());
        }
        return buf;
    }
}

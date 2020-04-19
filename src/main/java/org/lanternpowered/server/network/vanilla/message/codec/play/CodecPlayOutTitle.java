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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;

public final class CodecPlayOutTitle implements Codec<MessagePlayOutTitle> {

    private static final int SET_TITLE = 0;
    private static final int SET_SUBTITLE = 1;
    private static final int SET_ACTIONBAR_TITLE = 2;
    private static final int SET_TIMES = 3;
    private static final int CLEAR = 4;
    private static final int RESET = 5;

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutTitle message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        if (message instanceof MessagePlayOutTitle.Clear) {
            buf.writeVarInt(CLEAR);
        } else if (message instanceof MessagePlayOutTitle.Reset) {
            buf.writeVarInt(RESET);
        } else if (message instanceof MessagePlayOutTitle.SetTitle) {
            buf.writeVarInt(SET_TITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((MessagePlayOutTitle.SetTitle) message).getTitle());
        } else if (message instanceof MessagePlayOutTitle.SetSubtitle) {
            buf.writeVarInt(SET_SUBTITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((MessagePlayOutTitle.SetSubtitle) message).getTitle());
        } else if (message instanceof MessagePlayOutTitle.SetActionbarTitle) {
            buf.writeVarInt(SET_ACTIONBAR_TITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((MessagePlayOutTitle.SetActionbarTitle) message).getTitle());
        } else {
            final MessagePlayOutTitle.SetTimes message0 = (MessagePlayOutTitle.SetTimes) message;
            buf.writeVarInt(SET_TIMES);
            buf.writeInteger(message0.getFadeIn());
            buf.writeInteger(message0.getStay());
            buf.writeInteger(message0.getFadeOut());
        }
        return buf;
    }
}

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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTitle;

public final class CodecPlayOutTitle implements Codec<PacketPlayOutTitle> {

    private static final int SET_TITLE = 0;
    private static final int SET_SUBTITLE = 1;
    private static final int SET_ACTIONBAR_TITLE = 2;
    private static final int SET_TIMES = 3;
    private static final int CLEAR = 4;
    private static final int RESET = 5;

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutTitle packet) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        if (packet instanceof PacketPlayOutTitle.Clear) {
            buf.writeVarInt(CLEAR);
        } else if (packet instanceof PacketPlayOutTitle.Reset) {
            buf.writeVarInt(RESET);
        } else if (packet instanceof PacketPlayOutTitle.SetTitle) {
            buf.writeVarInt(SET_TITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((PacketPlayOutTitle.SetTitle) packet).getTitle());
        } else if (packet instanceof PacketPlayOutTitle.SetSubtitle) {
            buf.writeVarInt(SET_SUBTITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((PacketPlayOutTitle.SetSubtitle) packet).getTitle());
        } else if (packet instanceof PacketPlayOutTitle.SetActionbarTitle) {
            buf.writeVarInt(SET_ACTIONBAR_TITLE);
            context.write(buf, ContextualValueTypes.TEXT, ((PacketPlayOutTitle.SetActionbarTitle) packet).getTitle());
        } else {
            final PacketPlayOutTitle.SetTimes message0 = (PacketPlayOutTitle.SetTimes) packet;
            buf.writeVarInt(SET_TIMES);
            buf.writeInteger(message0.getFadeIn());
            buf.writeInteger(message0.getStay());
            buf.writeInteger(message0.getFadeOut());
        }
        return buf;
    }
}

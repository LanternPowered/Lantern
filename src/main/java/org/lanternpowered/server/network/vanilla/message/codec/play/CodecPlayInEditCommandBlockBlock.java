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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInEditCommandBlock;
import org.spongepowered.math.vector.Vector3i;

public final class CodecPlayInEditCommandBlockBlock implements Codec<MessagePlayInEditCommandBlock.Block> {

    @Override
    public MessagePlayInEditCommandBlock.Block decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final Vector3i pos = buf.readPosition();
        final String command = buf.readString();
        final MessagePlayInEditCommandBlock.Block.Mode mode = MessagePlayInEditCommandBlock.Block.Mode.values()[buf.readVarInt()];
        final byte flags = buf.readByte();
        final boolean shouldTrackOutput = (flags & 0x1) != 0;
        final boolean conditional = (flags & 0x2) != 0;
        final boolean automatic = (flags & 0x4) != 0;
        return new MessagePlayInEditCommandBlock.Block(pos, command, shouldTrackOutput, mode, conditional, automatic);
    }
}

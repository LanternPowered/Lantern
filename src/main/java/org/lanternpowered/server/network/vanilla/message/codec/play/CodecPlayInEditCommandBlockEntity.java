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

public final class CodecPlayInEditCommandBlockEntity implements Codec<MessagePlayInEditCommandBlock.Entity> {

    @Override
    public MessagePlayInEditCommandBlock.Entity decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int entityId = buf.readVarInt();
        final String command = buf.readString();
        final boolean shouldTrackOutput = buf.readBoolean();
        return new MessagePlayInEditCommandBlock.Entity(entityId, command, shouldTrackOutput);
    }
}

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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerAbilities;

public final class CodecPlayInPlayerAbilities implements Codec<MessagePlayInPlayerAbilities> {

    @Override
    public MessagePlayInPlayerAbilities decode(CodecContext context, ByteBuffer buf) throws CodecException {
        boolean flying = (buf.readByte() & 0x02) != 0;
        buf.readFloat();
        buf.readFloat();
        return new MessagePlayInPlayerAbilities(flying);
    }
}

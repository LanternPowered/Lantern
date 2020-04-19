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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerLook;

public final class CodecPlayInPlayerLook implements Codec<MessagePlayInPlayerLook> {

    @Override
    public MessagePlayInPlayerLook decode(CodecContext context, ByteBuffer buf) throws CodecException {
        float yaw = buf.readFloat();
        float pitch = buf.readFloat();
        boolean onGround = buf.readBoolean();
        return new MessagePlayInPlayerLook(yaw, pitch , onGround);
    }
}

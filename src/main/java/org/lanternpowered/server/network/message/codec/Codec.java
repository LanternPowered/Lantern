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
package org.lanternpowered.server.network.message.codec;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Message;

public interface Codec<M extends Message> {

    /**
     * Encodes the message into a byte buffer.
     *
     * @param context the codec context
     * @param message the message
     * @return the byte buffer
     */
    default ByteBuffer encode(CodecContext context, M message) throws CodecException {
        throw new EncoderException("Encoding through this codec (" + this.getClass().getName() + ") isn't supported!");
    }

    /**
     * Decodes the message from a byte buffer.
     *
     * @param context the codec context
     * @param buf the byte buffer
     * @return the message
     */
    default M decode(CodecContext context, ByteBuffer buf) throws CodecException {
        throw new DecoderException("Decoding through this codec (" + this.getClass().getName() + ") isn't supported!");
    }
}

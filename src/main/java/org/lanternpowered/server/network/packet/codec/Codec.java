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
package org.lanternpowered.server.network.packet.codec;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.PacketDecoder;
import org.lanternpowered.server.network.packet.PacketEncoder;

public interface Codec<P extends Packet> extends PacketEncoder<P>, PacketDecoder<P> {

    /**
     * Encodes the message into a byte buffer.
     *
     * @param ctx the codec context
     * @param packet the message
     * @return the byte buffer
     */
    @Override
    default ByteBuffer encode(CodecContext ctx, P packet) throws CodecException {
        throw new EncoderException("Encoding through this codec (" + this.getClass().getName() + ") isn't supported!");
    }

    /**
     * Decodes the message from a byte buffer.
     *
     * @param ctx the codec context
     * @param buf the byte buffer
     * @return the message
     */
    @Override
    default P decode(CodecContext ctx, ByteBuffer buf) throws CodecException {
        throw new DecoderException("Decoding through this codec (" + this.getClass().getName() + ") isn't supported!");
    }
}

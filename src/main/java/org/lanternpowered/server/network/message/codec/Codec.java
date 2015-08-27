package org.lanternpowered.server.network.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.Message;

public interface Codec<T extends Message> {

    /**
     * Encodes the message into a byte buffer.
     * 
     * @param context the codec context
     * @param message the message
     * @return the byte buffer
     */
    ByteBuf encode(CodecContext context, T message) throws CodecException;

    /**
     * Decodes the message from a byte buffer.
     * 
     * @param context the codec context
     * @param buf the byte buffer
     * @return the message
     */
    T decode(CodecContext context, ByteBuf buf) throws CodecException;
}

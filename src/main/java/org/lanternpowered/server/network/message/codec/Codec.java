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

    /**
     * Generates a hash for the context and message that is used
     * for the caching system of messages.
     * 
     * @param context the context
     * @return the hash code
     */
    // int encodeCachingHash(CodecContext context, T message);
}

package org.lanternpowered.server.network.message.processor;

import io.netty.handler.codec.CodecException;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;

/**
 * A processor for messages that aren't ready to be send to the client, this
 * processor allows messages to be translated and modified before they are send.
 * 
 * Example: The payload message is used for multiple purposes, this way we can
 * split it into: - Rename item message - Offer change message - ...
 */
public interface Processor<T extends Message> {

    /**
     * Processes the specified message.
     * 
     * @param context the codec context
     * @param message the message
     * @param output the output
     */
    void process(CodecContext context, T message, List<Message> output) throws CodecException;
}

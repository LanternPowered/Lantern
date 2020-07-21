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
package org.lanternpowered.server.network.message.processor;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.Packet;
import org.lanternpowered.server.network.message.codec.CodecContext;

import java.util.List;

/**
 * A processor for messages that aren't ready to be send to the client, this
 * processor allows messages to be translated and modified before they are send.
 *
 * Example: The payload message is used for multiple purposes, this way we can
 * split it into: - Rename item message - Offer change message - ...
 */
public interface Processor<M> {

    /**
     * Processes the specified message.
     *
     * @param context the codec context
     * @param message the message
     * @param output the output
     */
    void process(CodecContext context, M message, List<Packet> output) throws CodecException;

}

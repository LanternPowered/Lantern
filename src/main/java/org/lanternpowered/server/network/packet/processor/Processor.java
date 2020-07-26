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
package org.lanternpowered.server.network.packet.processor;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.codec.CodecContext;

import java.util.List;

/**
 * A processor for messages that aren't ready to be send to the client, this
 * processor allows messages to be translated and modified before they are send.
 *
 * Example: The payload message is used for multiple purposes, this way we can
 * split it into: - Rename item message - Offer change message - ...
 */
public interface Processor<P> {

    /**
     * Processes the specified packet.
     *
     * @param context the codec context
     * @param packet the packet
     * @param output the output
     */
    void process(CodecContext context, P packet, List<Packet> output) throws CodecException;

}

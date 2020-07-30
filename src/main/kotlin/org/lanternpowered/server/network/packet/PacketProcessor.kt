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
package org.lanternpowered.server.network.packet

import org.lanternpowered.server.network.packet.codec.CodecContext

/**
 * A processor for packets that aren't ready to be send to the client, this
 * processor allows packets to be translated and modified before they are send.
 *
 * Example: The payload packet is used for multiple purposes, this way we can
 * split it into: - Rename item packet - Offer change packet - ...
 */
interface PacketProcessor<P> {

    /**
     * Processes the specified packet.
     *
     * @param context The codec context
     * @param packet The input packet that will be processed
     * @param output The output packets
     */
    fun process(context: CodecContext, packet: P, output: MutableList<Packet>)
}

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
package org.lanternpowered.server.network.vanilla.packet.processor.play

import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.packet.PacketProcessor
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket

/**
 * This processor will separate the entries with different types and put them
 * into a new message, this is required because the vanilla codec can only use
 * one entry type for one message.
 */
object TabListProcessor : PacketProcessor<TabListPacket> {

    override fun process(context: CodecContext, packet: TabListPacket, output: MutableList<Packet>) {
        if (packet.entries.isEmpty())
            return
        if (packet.entries.size == 1) {
            output += packet
            return
        }
        val mapped = packet.entries
                .groupBy { entry -> entry.javaClass }
        if (mapped.size == 1) {
            output += packet
        } else {
            for ((_, value) in mapped)
                output += TabListPacket(value)
        }
    }
}

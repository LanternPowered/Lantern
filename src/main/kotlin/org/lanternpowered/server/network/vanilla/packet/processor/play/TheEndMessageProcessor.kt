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
import org.lanternpowered.server.network.vanilla.packet.type.play.TheEndPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.internal.ChangeGameStatePacket

object TheEndMessageProcessor : PacketProcessor<TheEndPacket> {

    override fun process(context: CodecContext, packet: TheEndPacket, output: MutableList<Packet>) {
        output.add(ChangeGameStatePacket(4, if (packet.playCredits) 1f else 0f))
    }
}

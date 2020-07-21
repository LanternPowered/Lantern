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

import org.lanternpowered.server.network.message.Packet
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.message.processor.Processor
import org.lanternpowered.server.network.vanilla.packet.type.play.SetGameModePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.internal.ChangeGameStatePacket
import org.lanternpowered.server.registry.type.data.GameModeRegistry

class SetGameModeProcessor : Processor<SetGameModePacket> {

    override fun process(context: CodecContext, packet: SetGameModePacket, output: MutableList<Packet>) {
        output.add(ChangeGameStatePacket(3, GameModeRegistry.getId(packet.gameMode).toFloat()))
    }
}

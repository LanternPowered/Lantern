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
package org.lanternpowered.server.network.vanilla.packet.handler.play

import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ChangeAdvancementTreePacket
import org.lanternpowered.server.registry.type.advancement.AdvancementTreeRegistry
import org.lanternpowered.api.key.NamespacedKey

class ClientAdvancementTreeHandler : PacketHandler<ChangeAdvancementTreePacket> {

    override fun handle(context: NetworkContext, packet: ChangeAdvancementTreePacket) {
        if (packet is ChangeAdvancementTreePacket.Open) {
            val id = packet.id
            context.session.player.offer(LanternKeys.OPEN_ADVANCEMENT_TREE,
                    AdvancementTreeRegistry.require(NamespacedKey.resolve(id)))
        } else {
            // Do we need the close event?
        }
    }
}

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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRecipeBookStatePacket

object ClientRecipeBookStatesHandler : PacketHandler<ClientRecipeBookStatePacket> {

    override fun handle(context: NetworkContext, packet: ClientRecipeBookStatePacket) {
        val player = context.session.player
        val key = when (packet.type) {
            ClientRecipeBookStatePacket.Type.CRAFTING -> LanternKeys.CRAFTING_RECIPE_BOOK_STATE
            ClientRecipeBookStatePacket.Type.FURNACE -> LanternKeys.FURNACE_RECIPE_BOOK_STATE
            ClientRecipeBookStatePacket.Type.BLAST_FURNACE -> LanternKeys.BLAST_FURNACE_RECIPE_BOOK_STATE
            ClientRecipeBookStatePacket.Type.SMOKER -> LanternKeys.SMOKER_RECIPE_BOOK_STATE
        }
        player.offer(key, packet.state)
    }
}

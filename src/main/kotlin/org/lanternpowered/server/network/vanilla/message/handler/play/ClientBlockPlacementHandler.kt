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
package org.lanternpowered.server.network.vanilla.message.handler.play

import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.message.handler.Handler
import org.lanternpowered.server.network.vanilla.message.type.play.ClientBlockPlacementMessage

class ClientBlockPlacementHandler : Handler<ClientBlockPlacementMessage> {

    override fun handle(context: NetworkContext, message: ClientBlockPlacementMessage) {
        val player = context.session.player
        player.resetIdleTimeoutCounter()
        player.resetOpenedSignPosition()
        player.interactionHandler.handleBlockPlacing(message)
    }
}

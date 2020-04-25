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

import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.message.handler.Handler
import org.lanternpowered.server.network.vanilla.message.type.play.ResourcePackStatusMessage

class ResourcePackStatusHandler : Handler<ResourcePackStatusMessage> {

    override fun handle(context: NetworkContext, message: ResourcePackStatusMessage) {
        val resourcePack = context.session.player.resourcePackSendQueue.poll(message.status)
        val player = context.session.player
        if (resourcePack == null) {
            Lantern.getLogger().warn("{} received a unexpected resource pack status message ({}), no resource pack was pending",
                    player.name, message.status)
            return
        }
        val causeStack = CauseStack.current()
        causeStack.withFrame { frame ->
            frame.addContext(CauseContextKeys.PLAYER, player)
            val event = LanternEventFactory.createResourcePackStatusEvent(
                    frame.currentCause, resourcePack, player, message.status)
            EventManager.post(event)
        }
    }
}

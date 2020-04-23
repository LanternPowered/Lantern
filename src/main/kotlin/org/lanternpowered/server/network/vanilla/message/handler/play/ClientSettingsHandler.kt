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

import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.message.handler.Handler
import org.lanternpowered.server.network.vanilla.message.type.play.ClientSettingsMessage
import org.lanternpowered.server.registry.type.data.SkinPartRegistry
import org.spongepowered.api.data.Keys

class ClientSettingsHandler : Handler<ClientSettingsMessage> {

    override fun handle(context: NetworkContext, message: ClientSettingsMessage) {
        val player = context.session.player
        val cause = causeOf(player)
        val skinParts = SkinPartRegistry.fromBitPattern(message.skinPartsBitPattern)
        val event = LanternEventFactory.createPlayerChangeClientSettingsEvent(
                cause, message.chatVisibility, skinParts, message.locale, player,
                message.enableColors, message.viewDistance)
        EventManager.post(event)
        player.locale = event.locale
        player.viewDistance = event.viewDistance
        player.chatVisibility = event.chatVisibility
        player.isChatColorsEnabled = message.enableColors
        player.offer(LanternKeys.DISPLAYED_SKIN_PARTS, event.displayedSkinParts)
        player.offer(Keys.DOMINANT_HAND, message.dominantHand)
    }
}

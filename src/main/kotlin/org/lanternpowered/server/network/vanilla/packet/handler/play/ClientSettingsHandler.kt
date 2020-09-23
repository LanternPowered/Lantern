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

import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSettingsPacket
import org.lanternpowered.server.registry.type.data.SkinPartRegistry
import org.lanternpowered.api.data.Keys

object ClientSettingsHandler : PacketHandler<ClientSettingsPacket> {

    override fun handle(context: NetworkContext, packet: ClientSettingsPacket) {
        val player = context.session.player
        val cause = causeOf(player)
        val skinParts = SkinPartRegistry.fromBitPattern(packet.skinPartsBitPattern)
        val event = LanternEventFactory.createPlayerChangeClientSettingsEvent(
                cause, packet.chatVisibility, skinParts, packet.locale, player,
                packet.enableColors, packet.viewDistance)
        EventManager.post(event)
        player.locale = event.locale
        player.viewDistance = event.viewDistance
        player.chatVisibility = event.chatVisibility
        player.isChatColorsEnabled = packet.enableColors
        player.offer(LanternKeys.DISPLAYED_SKIN_PARTS, event.displayedSkinParts)
        player.offer(Keys.DOMINANT_HAND, packet.dominantHand)
    }
}

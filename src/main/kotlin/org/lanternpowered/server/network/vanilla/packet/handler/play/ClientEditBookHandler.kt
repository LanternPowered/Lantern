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

import org.lanternpowered.api.data.eq
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifyBookPacket
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.item.ItemTypes

object ClientEditBookHandler : PacketHandler<ClientModifyBookPacket.Edit> {

    override fun handle(context: NetworkContext, packet: ClientModifyBookPacket.Edit) {
        val player = context.session.player
        val slot = player.inventory.hotbar.selectedSlot
        val itemStack = slot.peek()
        if (itemStack.type eq ItemTypes.WRITABLE_BOOK) {
            itemStack.offer(Keys.PLAIN_PAGES, packet.pages)
            slot.set(itemStack)
        }
    }
}

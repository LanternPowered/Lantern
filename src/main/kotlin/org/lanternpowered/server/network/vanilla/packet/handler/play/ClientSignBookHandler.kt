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
import org.lanternpowered.api.data.neq
import org.lanternpowered.api.item.ItemTypes
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.item.inventory.stack.isNotEmpty
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifyBookPacket
import org.lanternpowered.api.data.Keys
import kotlin.streams.toList

object ClientSignBookHandler : PacketHandler<ClientModifyBookPacket.Sign> {

    override fun handle(context: NetworkContext, packet: ClientModifyBookPacket.Sign) {
        val player = context.session.player
        val slot = player.inventory.hotbar.selectedSlot
        val itemStack: ItemStack = slot.peek()

        if (itemStack.isNotEmpty && itemStack.type eq ItemTypes.WRITABLE_BOOK) {
            val writtenBookStack = itemStackOf(ItemTypes.WRITTEN_BOOK)
            itemStack.values.asSequence()
                    .filter { value -> value.key neq Keys.PLAIN_PAGES }
                    .forEach { value -> writtenBookStack.offer(value) }
            writtenBookStack.offer(Keys.PAGES, packet.pages.stream().map(::textOf).toList())
            writtenBookStack.offer(Keys.AUTHOR, textOf(packet.author))
            writtenBookStack.offer(Keys.DISPLAY_NAME, textOf(packet.title))
            slot.set(writtenBookStack)
        }
    }
}

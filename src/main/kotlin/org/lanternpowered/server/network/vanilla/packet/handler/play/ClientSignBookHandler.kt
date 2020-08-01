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

import org.lanternpowered.api.item.inventory.isNotEmpty
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.handler.Handler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifyBookPacket
import org.spongepowered.api.data.Keys
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.Slot
import kotlin.streams.toList

object ClientSignBookHandler : Handler<ClientModifyBookPacket.Sign> {

    override fun handle(context: NetworkContext, packet: ClientModifyBookPacket.Sign) {
        val player = context.session.player
        val slot: Slot = null as Slot // TODO
        val itemStack: ItemStack = slot.peek()

        if (itemStack.isNotEmpty && itemStack.type === ItemTypes.WRITABLE_BOOK) {
            val writtenBookStack = itemStackOf(ItemTypes.WRITTEN_BOOK)
            itemStack.values.stream()
                    .filter { value -> value.key != Keys.PLAIN_PAGES.get() }
                    .forEach { value -> writtenBookStack.offer(value) }
            writtenBookStack.offer(Keys.PAGES, packet.pages.stream().map(::textOf).toList())
            writtenBookStack.offer(Keys.AUTHOR, textOf(packet.author))
            writtenBookStack.offer(Keys.DISPLAY_NAME, textOf(packet.title))
            slot.set(writtenBookStack)
        }
    }
}

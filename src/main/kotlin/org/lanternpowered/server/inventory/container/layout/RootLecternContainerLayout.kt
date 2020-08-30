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
package org.lanternpowered.server.inventory.container.layout

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.LecternClickAction
import org.lanternpowered.api.item.inventory.container.layout.LecternContainerLayout
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootLecternContainerLayout : LanternContainerLayout(
        title = TITLE, slotFlags = INVENTORY_FLAGS, propertyCount = 1
), LecternContainerLayout {

    companion object {

        private val TITLE = translatableTextOf("container.lectern")

        private val INVENTORY_FLAGS = intArrayOf(0)

        private const val PAGE = 0
    }

    private val onClick = ArrayList<(Player, LecternClickAction) -> Unit>()

    override val book: ContainerSlot get() = this[0]

    override var page: Int = 0
        set(value) {
            field = value
            // Update the client property
            this.setProperty(PAGE, value)
        }

    override fun onClick(fn: (player: Player, action: LecternClickAction) -> Unit) {
        this.onClick += fn
    }

    override fun createOpenPacket(data: ContainerData): Packet = OpenWindowPacket(data.containerId, ClientWindowTypes.LECTERN, this.title)

    override fun handleButtonClick(player: Player, index: Int) {
        val action = when {
            index == 1 -> LecternClickAction.PreviousPage
            index == 2 -> LecternClickAction.NextPage
            index == 3 -> LecternClickAction.Pickup
            index >= 100 -> LecternClickAction.ToPage(index - 100)
            else -> return
        }
        for (onClick in this.onClick)
            onClick(player, action)
    }
}

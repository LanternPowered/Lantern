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

import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootShulkerBoxContainerLayout : LanternTopBottomContainerLayout<GridContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private const val WIDTH = 9
        private const val HEIGHT = 3

        private val TITLE = textOf("container.shulkerBox")

        private val TOP_INVENTORY_FLAGS = IntArray(WIDTH * HEIGHT) { Flags.REVERSE_SHIFT_INSERTION }

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS
    }

    override fun createOpenPacket(data: ContainerData): Packet = OpenWindowPacket(data.containerId, ClientWindowTypes.SHULKER_BOX, this.title)
    override val top: GridContainerLayout = SubGridContainerLayout(0, WIDTH, HEIGHT, this)
}

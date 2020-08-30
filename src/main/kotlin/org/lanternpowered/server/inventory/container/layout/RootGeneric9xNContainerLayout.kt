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
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootGeneric9xNContainerLayout(val rows: Int) : LanternTopBottomContainerLayout<GridContainerLayout>(
        title = TITLE[rows - 1], slotFlags = ALL_INVENTORY_FLAGS[rows - 1]
) {

    companion object {

        private const val WIDTH = 9
        private const val MIN_ROWS = 1
        private const val MAX_ROWS = 6

        private val TOP_INVENTORY_FLAGS = Array(MAX_ROWS - MIN_ROWS + 1) { index ->
            IntArray((index + 1) * WIDTH) { Flags.REVERSE_SHIFT_INSERTION }
        }

        private val ALL_INVENTORY_FLAGS = Array(TOP_INVENTORY_FLAGS.size) { index ->
            TOP_INVENTORY_FLAGS[index] + MAIN_INVENTORY_FLAGS
        }

        private val TITLE = Array<Text>(TOP_INVENTORY_FLAGS.size) { index -> textOf("Generic 9x${index + 1}") }

        private val CLIENT_WINDOW_TYPE = Array(TOP_INVENTORY_FLAGS.size) {
            index -> ClientWindowTypes.get("generic_9x${index + 1}")
        }
    }

    override fun createOpenPacket(data: ContainerData): Packet = OpenWindowPacket(data.containerId, CLIENT_WINDOW_TYPE[this.rows - 1], this.title)
    override val top: GridContainerLayout = SubGridContainerLayout(0, WIDTH, this.rows, this)
}

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

import org.lanternpowered.api.item.inventory.container.layout.CraftingContainerLayout
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.client.ClientContainer
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootCraftingContainerLayout : LanternTopBottomContainerLayout<CraftingContainerLayout>(
        TITLE, ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("container.crafting")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.DISABLE_SHIFT_INSERTION + Flags.REVERSE_SHIFT_INSERTION + ClientContainer.FLAG_IGNORE_DOUBLE_CLICK, // Output slot
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 1
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 2
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 3
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 4
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 5
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 6
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 7
                Flags.DISABLE_SHIFT_INSERTION, // Input slot 8
                Flags.DISABLE_SHIFT_INSERTION  // Input slot 9
        )

        private val ALL_INVENTORY_FLAGS = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.CARTOGRAPHY, this.title))

    override val top: CraftingContainerLayout = SubCraftingContainerLayout(0, 3, 3, this)
}

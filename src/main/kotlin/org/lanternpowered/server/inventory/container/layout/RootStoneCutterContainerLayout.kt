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

import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.StoneCutterContainerLayout
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootStoneCutterContainerLayout : LanternTopBottomContainerLayout<StoneCutterContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("container.stonecutter")

        // TODO: Check flags
        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Input slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.DISABLE_SHIFT_INSERTION + Flags.IGNORE_DOUBLE_CLICK // Result slot
        )

        private val ALL_INVENTORY_FLAGS = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.STONE_CUTTER, this.title))

    override val top: StoneCutterContainerLayout = SubStoneCutterContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    // TODO: Add recipes and which recipe was selected
}

private class SubStoneCutterContainerLayout(
        offset: Int, size: Int, root: RootStoneCutterContainerLayout
) : SubContainerLayout(offset, size, root), StoneCutterContainerLayout {

    override val input: ContainerSlot get() = this[0]
    override val output: ContainerSlot get() = this[1]
}

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

import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.GrindstoneContainerLayout
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootGrindstoneContainerLayout : LanternTopBottomContainerLayout<GrindstoneContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("container.grindstone")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                0, // First input slot
                0, // Second input slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.DISABLE_SHIFT_INSERTION + Flags.IGNORE_DOUBLE_CLICK // Result slot
        )

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.GRINDSTONE, this.title))

    override val top: GrindstoneContainerLayout = SubGrindstoneContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)
}

private class SubGrindstoneContainerLayout(
        offset: Int, size: Int, root: RootGrindstoneContainerLayout
) : SubContainerLayout(offset, size, root), GrindstoneContainerLayout {

    override val inputs: ContainerLayout = SubContainerLayout(offset, this.size - 1, this.base)
    override val output: ContainerSlot get() = this[this.size - 1]
}

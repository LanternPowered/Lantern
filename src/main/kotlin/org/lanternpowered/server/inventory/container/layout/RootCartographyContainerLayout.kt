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

import org.lanternpowered.api.item.inventory.container.layout.CartographyContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootCartographyContainerLayout : LanternTopBottomContainerLayout<CartographyContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("container.cartography")

        // TODO: Check flags

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Map input slot
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Paper input slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.DISABLE_SHIFT_INSERTION or Flags.IGNORE_DOUBLE_CLICK // Result slot
        )

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.CARTOGRAPHY, this.title))

    override val top: CartographyContainerLayout = SubCartographyContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    override fun collectChangePackets(data: ContainerData, packets: MutableList<Packet>) {
        if (data.slotUpdateFlags[0] and UpdateFlags.NEEDS_UPDATE != 0 ||
                data.slotUpdateFlags[1] and UpdateFlags.NEEDS_UPDATE != 0) {
            // Force update the result slot if one of the inputs is modified
            data.queueSilentSlotChangeSafely(this.slots[2])
        }
        super.collectChangePackets(data, packets)
    }
}

private class SubCartographyContainerLayout(
        offset: Int, size: Int, root: RootCartographyContainerLayout
) : SubContainerLayout(offset, size, root), CartographyContainerLayout {
    override val map: ContainerSlot get() = this[0]
    override val paper: ContainerSlot get() = this[1]
    override val output: ContainerSlot get() = this[2]
}

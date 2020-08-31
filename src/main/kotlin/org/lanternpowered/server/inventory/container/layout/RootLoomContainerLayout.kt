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
import org.lanternpowered.api.item.inventory.container.layout.LoomContainerLayout
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootLoomContainerLayout : LanternTopBottomContainerLayout<LoomContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("container.loom")

        // TODO: Check flags

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Banner input slot
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Dye input slot
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Pattern input slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.DISABLE_SHIFT_INSERTION or Flags.IGNORE_DOUBLE_CLICK // Result slot
        )

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.LOOM, this.title))

    override val top: LoomContainerLayout = SubLoomContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    override fun collectChangePackets(data: ContainerData, packets: MutableList<Packet>) {
        if (data.slotUpdateFlags[0] and UpdateFlags.NEEDS_UPDATE != 0 ||
                data.slotUpdateFlags[1] and UpdateFlags.NEEDS_UPDATE != 0 ||
                data.slotUpdateFlags[2] and UpdateFlags.NEEDS_UPDATE != 0) {
            // Force update the result slot if one of the inputs is modified
            data.queueSilentSlotChangeSafely(this.slots[3])
        }
        super.collectChangePackets(data, packets)
    }

    // TODO: Add recipes and which recipe was selected
}

private class SubLoomContainerLayout(
        offset: Int, size: Int, root: RootLoomContainerLayout
) : SubContainerLayout(offset, size, root), LoomContainerLayout {
    override val banner: ContainerSlot get() = this[0]
    override val dye: ContainerSlot get() = this[1]
    override val pattern: ContainerSlot get() = this[2]
    override val output: ContainerSlot get() = this[3]
}

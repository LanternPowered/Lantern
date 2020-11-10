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
import org.lanternpowered.api.item.inventory.container.layout.CraftingContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.TopPlayerContainerLayout
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.inventory.client.ClientContainer
import org.lanternpowered.server.network.packet.Packet

class RootPlayerContainerLayout : LanternTopBottomContainerLayout<TopPlayerContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = textOf("Player")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.DISABLE_SHIFT_INSERTION + Flags.REVERSE_SHIFT_INSERTION + ClientContainer.FLAG_IGNORE_DOUBLE_CLICK, // Crafting output slot
                Flags.DISABLE_SHIFT_INSERTION, // Crafting input slot 1
                Flags.DISABLE_SHIFT_INSERTION, // Crafting input slot 2
                Flags.DISABLE_SHIFT_INSERTION, // Crafting input slot 3
                Flags.DISABLE_SHIFT_INSERTION, // Crafting input slot 4
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.silentSlotIndex(40), // Equipment slot 1
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.silentSlotIndex(39), // Equipment slot 2
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.silentSlotIndex(38), // Equipment slot 3
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.silentSlotIndex(37), // Equipment slot 4
                Flags.DISABLE_SHIFT_INSERTION + Flags.silentSlotIndex(41) // Offhand slot
        )

        val OFFHAND_INDEX = TOP_INVENTORY_FLAGS.size - 1
        const val ARMOR_SIZE = 4
        val ARMOR_START_INDEX = OFFHAND_INDEX - ARMOR_SIZE

        private val ALL_INVENTORY_FLAGS = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS
    }

    // The vanilla is the offhand slot the last index, after the main inventory,
    // but we modify this to move the slot before the main inventory

    override fun serverSlotIndexToClient(index: Int): Int {
        if (index == OFFHAND_INDEX)
            return ALL_INVENTORY_FLAGS.lastIndex
        if (index > OFFHAND_INDEX)
            return index - 1
        return index
    }

    override fun clientSlotIndexToServer(index: Int): Int {
        if (index == ALL_INVENTORY_FLAGS.lastIndex)
            return OFFHAND_INDEX
        if (index >= OFFHAND_INDEX)
            return index + 1
        return index
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> = throw UnsupportedOperationException()

    override val top: TopPlayerContainerLayout = SubTopPlayerContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)
}

private class SubTopPlayerContainerLayout(
        offset: Int, size: Int, val root: RootPlayerContainerLayout
) : SubContainerLayout(offset, size, root), TopPlayerContainerLayout {

    override val armor: GridContainerLayout =
            SubGridContainerLayout(RootPlayerContainerLayout.ARMOR_START_INDEX, 1, RootPlayerContainerLayout.ARMOR_SIZE, this.root)

    override val crafting: CraftingContainerLayout = SubCraftingContainerLayout(0, 2, 2, this.root)

    override val offhand: ContainerSlot = this[RootPlayerContainerLayout.OFFHAND_INDEX]
}


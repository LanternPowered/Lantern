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
import org.lanternpowered.api.item.inventory.container.layout.AnvilContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootAnvilContainerLayout : LanternTopBottomContainerLayout<AnvilContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS, propertyCount = 1
) {

    companion object {

        private val TITLE = translatableTextOf("container.repair")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                0, // First input slot
                0, // Second input slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.DISABLE_SHIFT_INSERTION or Flags.IGNORE_DOUBLE_CLICK // Result slot
        )

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS

        private const val REPAIR_COST_PROPERTY = 0
    }

    private val onChangeName = ArrayList<(Player, String) -> Unit>()

    override fun createOpenPacket(data: ContainerData): Packet = OpenWindowPacket(data.containerId, ClientWindowTypes.ANVIL, this.title)
    override val top: AnvilContainerLayout = SubAnvilContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    override fun collectChangePackets(data: ContainerData, packets: MutableList<Packet>) {
        if (data.slotUpdateFlags[0] and UpdateFlags.NEEDS_UPDATE != 0 ||
                data.slotUpdateFlags[1] and UpdateFlags.NEEDS_UPDATE != 0) {
            // Force update the result slot if one of the inputs is modified
            data.queueSilentSlotChangeSafely(this.slots[2])
        }
        super.collectChangePackets(data, packets)
    }

    var repairCost: Int = 0
        set(value) {
            field = value
            // Update the client property
            this.setProperty(REPAIR_COST_PROPERTY, value)
        }

    fun onChangeName(fn: (player: Player, name: String) -> Unit) {
        this.onChangeName += fn
    }

    /**
     * Handles a rename packet for the given player.
     */
    fun handleRename(player: Player, name: String) {
        val data = this.getData(player) ?: return
        // Force the output slot to update
        data.queueSilentSlotChange(this.slots[2])

        for (onChangeName in this.onChangeName)
            onChangeName(player, name)
    }
}

private class SubAnvilContainerLayout(
        offset: Int, size: Int, private val root: RootAnvilContainerLayout
) : SubContainerLayout(offset, size, root), AnvilContainerLayout {

    override val inputs: ContainerLayout = SubContainerLayout(offset, this.size - 1, this.base)
    override val output: ContainerSlot get() = this[this.size - 1]

    override var repairCost: Int
        get() = this.root.repairCost
        set(value) { this.root.repairCost = value }

    override fun onChangeName(fn: (player: Player, name: String) -> Unit) = this.root.onChangeName(fn)
}

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
import org.lanternpowered.api.item.inventory.container.layout.LoomContainerLayout
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket
import org.lanternpowered.server.registry.type.data.BannerPatternShapeRegistry
import org.spongepowered.api.data.type.BannerPatternShape
import org.spongepowered.api.data.type.BannerPatternShapes

class RootLoomContainerLayout : LanternTopBottomContainerLayout<LoomContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS, propertyCount = 1
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

        private val ALL_INVENTORY_FLAGS = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS

        private const val SELECTED_SHAPE = 0
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.LOOM, this.title))

    override val top: LoomContainerLayout = SubLoomContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    private val onClickShape = mutableListOf<(Player, BannerPatternShape) -> Unit>()

    var selectedShape: BannerPatternShape = BannerPatternShapes.BASE.get()
        set(value) {
            field = value
            // Update the client property
            this.setProperty(SELECTED_SHAPE, BannerPatternShapeRegistry.getId(value))
        }

    override fun collectChangePackets(data: ContainerData, packets: MutableList<Packet>) {
        if (data.slotUpdateFlags[0] and UpdateFlags.NEEDS_UPDATE != 0 ||
                data.slotUpdateFlags[1] and UpdateFlags.NEEDS_UPDATE != 0 ||
                data.slotUpdateFlags[2] and UpdateFlags.NEEDS_UPDATE != 0) {
            // Force update the result slot if one of the inputs is modified
            data.queueSilentSlotChangeSafely(this.slots[3])
        }
        super.collectChangePackets(data, packets)
    }

    override fun handleButtonClick(player: Player, index: Int) {
        // These shapes can't be selected through the buttons, this is the base shape
        // and shapes which require pattern papers
        if (index < 1 || index >= BannerPatternShapeRegistry.getId(BannerPatternShapes.GLOBE.get()))
            return
        val shape = BannerPatternShapeRegistry.require(index)
        for (onClickShape in this.onClickShape)
            onClickShape(player, shape)
    }

    fun onClickShape(fn: (player: Player, shape: BannerPatternShape) -> Unit) {
        this.onClickShape += fn
    }
}

private class SubLoomContainerLayout(
        offset: Int, size: Int, private val root: RootLoomContainerLayout
) : SubContainerLayout(offset, size, root), LoomContainerLayout {

    override val banner: ContainerSlot get() = this[0]
    override val dye: ContainerSlot get() = this[1]
    override val pattern: ContainerSlot get() = this[2]
    override val output: ContainerSlot get() = this[3]

    override var selectedShape: BannerPatternShape
        get() = this.root.selectedShape
        set(value) { this.root.selectedShape = value }

    override fun onClickShape(fn: (player: Player, shape: BannerPatternShape) -> Unit) =
            this.root.onClickShape(fn)
}

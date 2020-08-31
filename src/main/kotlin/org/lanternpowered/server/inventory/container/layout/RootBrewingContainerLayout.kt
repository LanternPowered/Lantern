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

import org.lanternpowered.api.item.inventory.container.layout.BrewingContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

class RootBrewingContainerLayout : LanternTopBottomContainerLayout<BrewingContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS, propertyCount = 2
) {

    companion object {

        private val TITLE = translatableTextOf("container.brewing")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.ONE_ITEM, // Bottle slot 1
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.ONE_ITEM, // Bottle slot 2
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.ONE_ITEM, // Bottle slot 3
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Potion ingredient slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION  // Blaze powder slot
        )

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS

        private const val BREW_PROGRESS_PROPERTY = 0
        private const val FUEL_PROGRESS_PROPERTY = 1
    }

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.BREWING_STAND, this.title))

    override val top: BrewingContainerLayout = SubBrewingContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    var brewProgress: Double = 0.0
        set(value) {
            field = value.coerceIn(0.0, 1.0)
            // Update the client property
            // 0.0 is full, 1.0 is empty, so it needs to be swapped
            this.setProperty(BREW_PROGRESS_PROPERTY, ((1.0 - field) * 400.0).toInt())
        }

    var fuelProgress: Double = 0.0
        set(value) {
            field = value.coerceIn(0.0, 1.0)
            // Update the client property
            this.setProperty(FUEL_PROGRESS_PROPERTY, (field * 20.0).toInt())
        }
}

private class SubBrewingContainerLayout(
        offset: Int, size: Int, private val root: RootBrewingContainerLayout
) : SubContainerLayout(offset, size, root), BrewingContainerLayout {

    override val bottles: ContainerLayout = SubContainerLayout(offset, this.size - 2, this.base)
    override val fuel: ContainerSlot get() = this[this.size - 1]
    override val ingredient: ContainerSlot get() = this[this.size - 2]

    override var brewProgress: Double
        get() = this.root.brewProgress
        set(value) { this.root.brewProgress = value }

    override var fuelProgress: Double
        get() = this.root.fuelProgress
        set(value) { this.root.fuelProgress = value }
}

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
import org.lanternpowered.api.item.inventory.container.layout.SmeltingContainerLayout
import org.lanternpowered.api.text.Text
import org.lanternpowered.server.inventory.container.ClientWindowType
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

@Suppress("LeakingThis")
abstract class RootSmeltingContainerLayout(title: Text) : LanternTopBottomContainerLayout<SmeltingContainerLayout>(
        title = title, slotFlags = ALL_INVENTORY_FLAGS, propertyCount = 4
) {

    companion object {

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Input slot
                Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Fuel slot
                Flags.DISABLE_SHIFT_INSERTION // Output slot
        )

        private val ALL_INVENTORY_FLAGS = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS

        private const val MAX_PROGRESS_PROPERTY_VALUE = 1000.0
        private const val SMELT_PROGRESS_PROPERTY = 2
        private const val MAX_SMELT_PROGRESS_PROPERTY = 3
        private const val FUEL_PROGRESS_PROPERTY = 0
        private const val MAX_FUEL_PROGRESS_PROPERTY = 1
    }

    init {
        this.setProperty(MAX_SMELT_PROGRESS_PROPERTY, MAX_PROGRESS_PROPERTY_VALUE.toInt())
        this.setProperty(MAX_FUEL_PROGRESS_PROPERTY, MAX_PROGRESS_PROPERTY_VALUE.toInt())
    }

    abstract val windowType: ClientWindowType

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, this.windowType, this.title))

    override val top: SmeltingContainerLayout = SubSmeltingContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    var smeltProgress: Double = 0.0
        set(value) {
            field = value.coerceIn(0.0, 1.0)
            // Update the client property
            this.setProperty(SMELT_PROGRESS_PROPERTY, (field * MAX_PROGRESS_PROPERTY_VALUE).toInt())
        }

    var fuelProgress: Double = 0.0
        set(value) {
            field = value.coerceIn(0.0, 1.0)
            // Update the client property
            this.setProperty(FUEL_PROGRESS_PROPERTY, (field * MAX_PROGRESS_PROPERTY_VALUE).toInt())
        }
}

private class SubSmeltingContainerLayout(
        offset: Int, size: Int, private val root: RootSmeltingContainerLayout
) : SubContainerLayout(offset, size, root), SmeltingContainerLayout {

    override val input: ContainerSlot get() = this[0]
    override val fuel: ContainerSlot get() = this[1]
    override val output: ContainerSlot get() = this[0]

    override var smeltProgress: Double
        get() = this.root.smeltProgress
        set(value) { this.root.smeltProgress = value }

    override var fuelProgress: Double
        get() = this.root.fuelProgress
        set(value) { this.root.fuelProgress = value }
}

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
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.TopBottomContainerLayout
import org.lanternpowered.api.text.Text

abstract class LanternTopBottomContainerLayout<T : ContainerLayout>(
        title: Text,
        slotFlags: IntArray,
        propertyCount: Int = 0
) : LanternContainerLayout(title, slotFlags, propertyCount), TopBottomContainerLayout<T> {

    companion object {

        const val MAIN_INVENTORY_WIDTH = 9
        const val MAIN_INVENTORY_HEIGHT = 4

        val MAIN_INVENTORY_FLAGS = IntArray(MAIN_INVENTORY_WIDTH * MAIN_INVENTORY_HEIGHT) { Flags.IS_MAIN_INVENTORY }

        init {
            val lastRowStart = MAIN_INVENTORY_WIDTH * (MAIN_INVENTORY_HEIGHT - 1)
            for (i in 0 until MAIN_INVENTORY_WIDTH) {
                // Apply hotbar flags
                MAIN_INVENTORY_FLAGS[lastRowStart + i] += (i + 1) shl Flags.HOTBAR_SHIFT
            }
        }
    }

    @Suppress("LeakingThis")
    override val bottom: GridContainerLayout =
            SubGridContainerLayout(slotFlags.size - MAIN_INVENTORY_FLAGS.size, MAIN_INVENTORY_WIDTH, MAIN_INVENTORY_HEIGHT, this)
}

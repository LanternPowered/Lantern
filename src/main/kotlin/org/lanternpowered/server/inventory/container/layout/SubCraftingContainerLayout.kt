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

class SubCraftingContainerLayout(
        offset: Int, width: Int, height: Int, base: LanternContainerLayout
) : SubContainerLayout(offset, width * height + 1, base), CraftingContainerLayout {

    // 0 = output slot
    // 1..last = input slots

    override val inputs: GridContainerLayout = SubGridContainerLayout(offset + 1, width, height, base)
    override val output: ContainerSlot get() = this[0]
}

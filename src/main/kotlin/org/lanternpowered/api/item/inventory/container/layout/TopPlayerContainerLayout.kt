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
package org.lanternpowered.api.item.inventory.container.layout

/**
 * Represents the top container layout of an anvil.
 */
interface TopPlayerContainerLayout : ContainerLayout {

    /**
     * The crafting layout in the players inventory.
     */
    val crafting: CraftingContainerLayout

    /**
     * The sub layout of the armor.
     */
    val armor: GridContainerLayout

    /**
     * The offhand slot.
     */
    val offhand: ContainerSlot
}

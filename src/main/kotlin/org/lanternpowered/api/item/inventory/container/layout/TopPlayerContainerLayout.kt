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
    fun crafting(): CraftingContainerLayout

    /**
     * The sub layout of the armor.
     */
    fun armor(): GridContainerLayout

    /**
     * The offhand slot.
     */
    fun offhand(): ContainerSlot
}

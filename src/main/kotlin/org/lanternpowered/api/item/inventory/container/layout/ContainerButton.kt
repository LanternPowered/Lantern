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

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.itemStackOf
import java.util.function.Supplier

/**
 * Represents a button behavior that was applied
 * to a container slot.
 */
interface ContainerButton {

    /**
     * The item that is currently displayed.
     */
    val icon: ItemStackSnapshot

    /**
     * Sets the current icon of the button.
     */
    fun icon(icon: Supplier<out ItemType>): ContainerButton =
            this.icon(itemStackOf(icon))

    /**
     * Sets the current icon of the button.
     */
    fun icon(icon: ItemType): ContainerButton =
            this.icon(itemStackOf(icon))

    /**
     * Sets the current icon of the button.
     */
    fun icon(icon: ItemStack): ContainerButton =
            this.icon(icon.createSnapshot())

    /**
     * Sets the current icon of the button.
     */
    fun icon(icon: ItemStackSnapshot): ContainerButton
}

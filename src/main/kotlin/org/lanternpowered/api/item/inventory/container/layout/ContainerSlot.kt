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

import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.slot.Slot

interface ContainerSlot {

    /**
     * The slot that is applied to this container slot, if any.
     */
    val slot: Slot?

    /**
     * The container button if this slot is marked as a button.
     */
    val button: ContainerButton?

    /**
     * Sets a fill item that will be displayed if this slot
     * isn't bound.
     */
    fun fill(item: ItemStackSnapshot)

    /**
     * Binds this slot as a "button". Will return the same instance
     * if the slot was bound as a button multiple times.
     */
    fun button(): ContainerButton

    /**
     * Binds this slot as a "button". Will return the same instance
     * if the slot was bound as a button multiple times.
     */
    fun button(fn: ContainerButton.() -> Unit): ContainerButton

    /**
     * Binds the given [Slot] to this container slot.
     *
     * @param slot The slot to bind
     */
    fun slot(slot: Slot)

    /**
     * Resets any slot or button that was applied to the container
     * slot. If a fill item was applied, this will be displayed
     * instead.
     */
    fun reset()
}

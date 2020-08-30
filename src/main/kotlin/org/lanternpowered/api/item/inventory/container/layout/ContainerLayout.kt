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

import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.text.Text

/**
 * Represents the layout of a container.
 */
interface ContainerLayout : Iterable<ContainerSlot> {

    /**
     * The size of the layout, in maximum slot count.
     */
    val size: Int

    /**
     * The title of the container.
     */
    val title: Text

    /**
     * Sets the title.
     *
     * Applying a title to a non root layout will
     * not have any effect.
     */
    fun title(title: Text)

    /**
     * Sets a fill item that will be displayed in every slot
     * index that wasn't bound. It is not possible to modify
     * these slots.
     */
    fun fill(item: ItemStackSnapshot) {
        for (containerSlot in this)
            containerSlot.fill(item)
    }

    /**
     * Gets the [ContainerSlot] at the given index.
     */
    operator fun get(index: Int): ContainerSlot

    /**
     * Gets a sub layout from this layout at the given offset and
     * number of slots (size). An [IndexOutOfBoundsException] will
     * be thrown if the offset is out of bounds or not enough slots
     * are available after the given offset.
     */
    fun range(offset: Int, size: Int): ContainerLayout

    /**
     * Gets a sub layout from this layout at the given start and
     * end index (inclusive). An [IndexOutOfBoundsException] will
     * be thrown if the start or end index is out of bounds.
     */
    fun range(range: IntRange): ContainerLayout

    /**
     * Applies all the slots of the given inventory to this
     * container layout.
     *
     * @param inventory The inventory of which the slots will
     *                  be applied
     */
    fun slots(inventory: Inventory) =
            this.slots(inventory.slots())

    /**
     * Applies all the slots of the given inventory to this
     * container layout.
     *
     * @param slots The slots that will be applied
     */
    fun slots(slots: Iterable<Slot>) =
            this.slotsAt(0, slots)

    /**
     * Applies all the slots of the given inventory to this
     * container layout at the given start index.
     *
     * @param index The start index to start setting slots at
     * @param inventory The inventory of which the slots will
     *                  be applied
     */
    fun slotsAt(index: Int, inventory: Inventory) =
            this.slotsAt(index, inventory.slots())

    /**
     * Applies all the slots of the given inventory to this
     * container layout at the given start index.
     *
     * @param index The start index to start setting slots at
     * @param slots The slots that will be applied
     */
    fun slotsAt(index: Int, slots: Iterable<Slot>) {
        var i = index
        for (slot in slots)
            this[i++].slot(slot)
    }

    /**
     * Resets all the container slots in this layout. Unbinds
     * all the slots and buttons.
     */
    fun reset() {
        for (containerSlot in this)
            containerSlot.reset()
    }
}

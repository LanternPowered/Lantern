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
package org.lanternpowered.api.item.inventory.container

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.ItemTypes
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.item.inventory.stack.asSnapshot
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.api.data.Keys
import java.util.function.Supplier

object ContainerFills {

    /**
     * A container fill item which is invisible.
     *
     * This is the default fill item.
     */
    val None: ItemStackSnapshot = emptyItemStackSnapshot()

    /**
     * A black container fill item which fills the complete slot.
     */
    val Black: ItemStackSnapshot = this.of(ItemTypes.BLACK_STAINED_GLASS_PANE)

    /**
     * A white container fill item which fills the complete slot.
     */
    val White: ItemStackSnapshot = this.of(ItemTypes.WHITE_STAINED_GLASS_PANE)

    /**
     * Creates a new container fill item with the given type.
     *
     * Fill items have an empty display name.
     */
    fun of(type: Supplier<out ItemType>): ItemStackSnapshot =
            this.of(type.get())

    /**
     * Creates a new container fill item with the given type.
     *
     * Fill items have an empty display name.
     */
    fun of(type: ItemType): ItemStackSnapshot {
        return itemStackOf(type) {
            add(Keys.DISPLAY_NAME, emptyText())
        }.asSnapshot()
    }
}

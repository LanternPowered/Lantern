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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.item.inventory

import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.math.vector.Vector2i
import java.util.Optional
import kotlin.contracts.contract

typealias Inventory2D = org.spongepowered.api.item.inventory.type.Inventory2D

/**
 * Gets the normal 2D inventory as an extended 2D inventory.
 */
inline fun Inventory2D.fix(): ExtendedInventory {
    contract { returns() implies (this@fix is ExtendedInventory2D) }
    return this as ExtendedInventory2D
}

/**
 * Gets the normal 2D inventory as an extended 2D inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedInventory2D.fix(): ExtendedInventory2D = this

/**
 * Get a slot at the given position. Returns `null` if the
 * position doesn't exist in the inventory 2D grid.
 */
inline fun Inventory2D.slot(position: Vector2i): ExtendedSlot? {
    contract { returns() implies (this@slot is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slot(position)
}

/**
 * Gets the position of the slot relative to this 2D inventory
 * grid. Returns `null` if the given slot doesn't exist in this
 * grid.
 */
inline fun Inventory2D.slotPosition(slot: Slot): Vector2i? {
    contract { returns() implies (this@slotPosition is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slotPosition(slot)
}

/**
 * An extended version of [Inventory].
 */
interface ExtendedInventory2D : ExtendedInventory, Inventory2D {

    /**
     * Get a slot at the given position. Returns `null` if the
     * position doesn't exist in the inventory 2D grid.
     */
    fun slot(position: Vector2i): ExtendedSlot?

    /**
     * Gets the position of the slot relative to this 2D inventory
     * grid. Returns `null` if the given slot doesn't exist in this
     * grid.
     */
    fun slotPosition(slot: Slot): Vector2i?

    override fun getSlot(position: Vector2i): Optional<Slot> =
            this.slot(position).asOptional()
}

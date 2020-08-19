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
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.math.vector.Vector2i
import java.util.Optional
import kotlin.contracts.contract

typealias Inventory2D = org.spongepowered.api.item.inventory.type.Inventory2D

/**
 * Gets the normal 2D inventories as an extended 2D inventories.
 */
inline fun List<Inventory2D>.fix(): List<ExtendedInventory2D> =
        this.uncheckedCast()

/**
 * Gets the normal 2D inventory as an extended 2D inventory.
 */
inline fun Inventory2D.fix(): ExtendedInventory2D {
    contract { returns() implies (this@fix is ExtendedInventory2D) }
    return this as ExtendedInventory2D
}

/**
 * Gets the normal 2D inventory as an extended 2D inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedInventory2D.fix(): ExtendedInventory2D = this

/**
 * Get a slot at the given position.
 *
 * Throws an [IllegalArgumentException] if the position is outside
 * the bounds of this 2D inventory.
 */
inline fun Inventory2D.slot(position: Vector2i): ExtendedSlot {
    contract { returns() implies (this@slot is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slot(position)
}

/**
 * Get a slot at the given position.
 *
 * Returns `null` if the position is outside the bounds of this 2D inventory.
 */
inline fun Inventory2D.slotOrNull(position: Vector2i): ExtendedSlot? {
    contract { returns() implies (this@slotOrNull is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slotOrNull(position)
}

/**
 * Get a slot at the given position.
 *
 * Throws an [IllegalArgumentException] if the position is outside
 * the bounds of this 2D inventory.
 */
inline fun Inventory2D.slot(x: Int, y: Int): ExtendedSlot {
    contract { returns() implies (this@slot is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slot(x, y)
}

/**
 * Get a slot at the given position.
 *
 * Returns `null` if the position is outside the bounds of this 2D inventory.
 */
inline fun Inventory2D.slotOrNull(x: Int, y: Int): ExtendedSlot? {
    contract { returns() implies (this@slotOrNull is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slotOrNull(x, y)
}

/**
 * Gets the position of the slot relative to this 2D inventory
 * grid.
 *
 * Throws [IllegalArgumentException] if the given slot is not part
 * of this 2D inventory.
 */
inline fun Inventory2D.slotPosition(slot: Slot): Vector2i {
    contract { returns() implies (this@slotPosition is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slotPosition(slot)
}

/**
 * Gets the position of the slot relative to this 2D inventory
 * grid.
 *
 * Returns `null` if the given slot is not part of this 2D inventory.
 */
inline fun Inventory2D.slotPositionOrNull(slot: Slot): Vector2i? {
    contract { returns() implies (this@slotPositionOrNull is ExtendedInventory2D) }
    return (this as ExtendedInventory2D).slotPositionOrNull(slot)
}

/**
 * An extended version of [Inventory2D].
 */
interface ExtendedInventory2D : ExtendedInventory, Inventory2D {

    /**
     * The width of this 2D inventory.
     */
    val width: Int

    /**
     * The height of this 2D inventory.
     */
    val height: Int

    /**
     * Get a slot at the given position.
     *
     * Throws an [IllegalArgumentException] if the position is outside
     * the bounds of this 2D inventory.
     */
    fun slot(position: Vector2i): ExtendedSlot =
            this.slotOrNull(position) ?: throw IllegalArgumentException(
                    "The position $position is outside the bounds of this 2D inventory")

    /**
     * Get a slot at the given position.
     *
     * Returns `null` if the position is outside the bounds of this 2D inventory.
     */
    fun slotOrNull(position: Vector2i): ExtendedSlot?

    /**
     * Get a slot at the given position.
     *
     * Throws an [IllegalArgumentException] if the position is outside
     * the bounds of this 2D inventory.
     */
    fun slot(x: Int, y: Int): ExtendedSlot =
            this.slot(Vector2i(x, y))

    /**
     * Get a slot at the given position.
     *
     * Returns `null` if the position is outside the bounds of this 2D inventory.
     */
    fun slotOrNull(x: Int, y: Int): ExtendedSlot? =
            this.slotOrNull(Vector2i(x, y))

    /**
     * Gets the position of the slot relative to this 2D inventory
     * grid.
     *
     * Throws [IllegalArgumentException] if the given slot is not part
     * of this 2D inventory.
     */
    fun slotPosition(slot: Slot): Vector2i =
            this.slotPositionOrNull(slot) ?: throw IllegalArgumentException(
                    "The slot $slot is not part of this 2D inventory.")

    /**
     * Gets the position of the slot relative to this 2D inventory
     * grid.
     *
     * Returns `null` if the given slot is not part of this 2D inventory.
     */
    fun slotPositionOrNull(slot: Slot): Vector2i?

    override fun getSlot(position: Vector2i): Optional<Slot> =
            this.slot(position).asOptional()
}

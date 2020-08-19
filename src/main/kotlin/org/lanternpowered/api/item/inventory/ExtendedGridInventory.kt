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

import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.math.vector.Vector2i
import java.util.Optional
import kotlin.contracts.contract

typealias GridInventory = org.spongepowered.api.item.inventory.type.GridInventory

/**
 * Gets the normal grid inventories as an extended grid inventories.
 */
inline fun List<GridInventory>.fix(): List<ExtendedGridInventory> =
        this.uncheckedCast()

/**
 * Gets the normal grid inventory as an extended grid inventory.
 */
inline fun GridInventory.fix(): ExtendedGridInventory {
    contract { returns() implies (this@fix is ExtendedGridInventory) }
    return this as ExtendedGridInventory
}

/**
 * Gets the normal grid inventory as an extended grid inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedGridInventory.fix(): ExtendedGridInventory = this

/**
 * An extended version of [GridInventory].
 */
interface ExtendedGridInventory : GridInventory, ExtendedInventory2D {

    /**
     * Gets the [ExtendedInventoryRow] for the given column y coordinate.
     *
     * Throws an [IndexOutOfBoundsException] if the given y coordinate isn't
     * within the bounds of this grid.
     */
    fun row(y: Int): ExtendedInventoryRow =
            this.rowOrNull(y) ?: throw IndexOutOfBoundsException(y)

    /**
     * Gets the [ExtendedInventoryRow] for the given column x coordinate.
     *
     * Returns `null` if the given y coordinate isn't within the bounds of this grid.
     */
    fun rowOrNull(y: Int): ExtendedInventoryRow?

    /**
     * Gets a list of all the [ExtendedInventoryRow] in this grid.
     */
    fun rows(): List<ExtendedInventoryRow>

    /**
     * Gets the index (y position) of the given [InventoryRow] within
     * this grid.
     *
     * Throws an [IllegalArgumentException] if the given row is not
     * part of this grid inventory.
     */
    fun rowIndex(row: InventoryRow): Int =
            this.rowIndexOrNull(row) ?: throw IllegalArgumentException("The row $row is not part of this grid.")

    /**
     * Gets the index (y position) of the given [InventoryRow] within
     * this grid.
     *
     * Returns `null` if the given row is not part of this grid inventory.
     */
    fun rowIndexOrNull(row: InventoryRow): Int?

    /**
     * Gets the [ExtendedInventoryColumn] for the given column x coordinate.
     *
     * Throws an [IndexOutOfBoundsException] if the given x coordinate isn't
     * within the bounds of this grid.
     */
    fun column(x: Int): ExtendedInventoryColumn =
            this.columnOrNull(x) ?: throw IndexOutOfBoundsException(x)

    /**
     * Gets the [ExtendedInventoryColumn] for the given column x coordinate.
     *
     * Returns `null` if the given x coordinate isn't within the bounds of this grid.
     */
    fun columnOrNull(x: Int): ExtendedInventoryColumn?

    /**
     * Gets a list of all the [ExtendedInventoryColumn] in this grid.
     */
    fun columns(): List<ExtendedInventoryColumn>

    /**
     * Gets the index (x position) of the given [InventoryColumn] within
     * this grid.
     *
     * Throws an [IllegalArgumentException] if the given column is not
     * part of this grid inventory.
     */
    fun columnIndex(column: InventoryColumn): Int =
            this.columnIndexOrNull(column) ?: throw IllegalArgumentException("The column $column is not part of this grid.")

    /**
     * Gets the index (x position) of the given [InventoryColumn] within
     * this grid.
     *
     * Returns `null` if the given column is not part of this grid inventory.
     */
    fun columnIndexOrNull(column: InventoryColumn): Int?

    @Deprecated(message = "Prefer to use rowOrNull(y).")
    override fun getRow(y: Int): Optional<InventoryRow>

    @Deprecated(message = "Prefer to use columnOrNull(x).")
    override fun getColumn(x: Int): Optional<InventoryColumn>

    @Deprecated(message = "Prefer to use slotOrNull(position).")
    override fun getSlot(position: Vector2i): Optional<Slot>
}

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
package org.lanternpowered.server.inventory

import org.lanternpowered.api.item.inventory.ExtendedGridInventory
import org.lanternpowered.api.item.inventory.ExtendedInventoryColumn
import org.lanternpowered.api.item.inventory.ExtendedInventoryRow
import org.lanternpowered.api.item.inventory.InventoryColumn
import org.lanternpowered.api.item.inventory.InventoryRow
import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.Slot
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.math.vector.Vector2i
import java.util.Optional

class LanternGridInventory : AbstractInventory2D(), ExtendedGridInventory {

    override fun getColumns(): Int = this.width
    override fun getRows(): Int = this.height

    override fun getDimensions(): Vector2i =
            Vector2i(this.width, this.height)

    override fun slotOrNull(position: Vector2i): ExtendedSlot? {
        TODO("Not yet implemented")
    }

    override fun slotPositionOrNull(slot: Slot): Vector2i? {
        TODO("Not yet implemented")
    }

    override fun rowOrNull(y: Int): ExtendedInventoryRow? {
        TODO("Not yet implemented")
    }

    override fun rows(): List<ExtendedInventoryRow> {
        TODO("Not yet implemented")
    }

    override fun rowIndexOrNull(row: InventoryRow): Int? {
        TODO("Not yet implemented")
    }

    override fun columnOrNull(x: Int): ExtendedInventoryColumn? {
        TODO("Not yet implemented")
    }

    override fun columns(): List<ExtendedInventoryColumn> {
        TODO("Not yet implemented")
    }

    override fun columnIndexOrNull(column: InventoryColumn): Int? {
        TODO("Not yet implemented")
    }

    override fun getRow(y: Int): Optional<InventoryRow> =
            this.rowOrNull(y).asOptional()

    override fun getColumn(x: Int): Optional<InventoryColumn> =
            this.columnOrNull(x).asOptional()

    override fun getSlot(position: Vector2i): Optional<Slot> =
            this.slotOrNull(position).asOptional()

    override fun getSlot(x: Int, y: Int): Optional<Slot> =
            this.slotOrNull(x, y).asOptional()

    override fun peek(x: Int, y: Int): Optional<ItemStack> =
            this.slotOrNull(x, y)?.peek().asOptional()

    override fun set(x: Int, y: Int, stack: ItemStack): InventoryTransactionResult =
            this.slotOrNull(x, y)?.forceSet(stack) ?: InventoryTransactionResults.rejectPollNoSlot()

    override fun poll(x: Int, y: Int): PollInventoryTransactionResult =
            this.slotOrNull(x, y)?.poll() ?: InventoryTransactionResults.rejectPollNoSlot()

    override fun poll(x: Int, y: Int, limit: Int): PollInventoryTransactionResult =
            this.slotOrNull(x, y)?.poll(limit) ?: InventoryTransactionResults.rejectPollNoSlot()
}

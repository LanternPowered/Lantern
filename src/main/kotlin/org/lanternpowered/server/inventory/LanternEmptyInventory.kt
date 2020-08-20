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

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ExtendedEmptyInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.ViewableInventory
import org.lanternpowered.api.item.inventory.emptyItemStack
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

open class LanternEmptyInventory : AbstractInventory(), ExtendedEmptyInventory {

    override fun empty(): LanternEmptyInventory = this
    override fun children(): List<AbstractInventory> = emptyList()
    override fun addSlotChangeListener(listener: (ExtendedSlot) -> Unit) {}
    override fun contains(stack: ItemStack): Boolean = false
    override fun contains(type: ItemType): Boolean = false
    override fun clear() {}
    override fun containsAny(stack: ItemStack): Boolean = false
    override fun toViewable(): ViewableInventory? = null
    override fun peekOfferAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) {}
    override fun offerAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) {}
    override fun pollFast(predicate: (ItemStackSnapshot) -> Boolean): ItemStack = emptyItemStack()
    override fun pollFast(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack = emptyItemStack()
    override fun peek(predicate: (ItemStackSnapshot) -> Boolean): ItemStack = emptyItemStack()
    override fun peek(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack = emptyItemStack()
    override fun freeCapacity(): Int = 0
    override fun capacity(): Int = 0
    override fun totalQuantity(): Int = 0
    override fun slotOrNull(index: Int): ExtendedSlot? = null
    override fun slotIndexOrNull(slot: Slot): Int? = null
    override fun slots(): List<AbstractSlot> = emptyList()
    override fun canContain(stack: ItemStack): Boolean = false

    override fun poll(predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult =
            InventoryTransactionResults.rejectPoll()

    override fun poll(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult =
            InventoryTransactionResults.rejectPoll()

    override fun <V : Any> get(child: Inventory, key: Key<out Value<V>>): Optional<V> = child.get(key)

    override fun instantiateView(): InventoryView<LanternEmptyInventory> = View(this)

    private class View(override val backing: LanternEmptyInventory) : LanternEmptyInventory(), InventoryView<LanternEmptyInventory>
}

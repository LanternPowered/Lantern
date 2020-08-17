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

import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.Slot
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import java.util.Optional

@Suppress("DEPRECATION")
abstract class AbstractSlot : AbstractMutableInventory(), ExtendedSlot {

    @Suppress("LeakingThis")
    private val slots = listOf(this)

    /**
     * The actual slot, will return itself if the stack is directly
     * stored in this slot. This will return another slot if this is
     * a forwarding slot.
     */
    abstract val actual: Slot

    /**
     * Direct access to the [ItemStack] that's stored in this slot.
     */
    abstract var rawItem: ItemStack

    /**
     * Adds the [SlotChangeTracker].
     *
     * @param tracker The slot change tracker
     */
    abstract fun addTracker(tracker: SlotChangeTracker)

    /**
     * Removes the [SlotChangeTracker].
     *
     * @param tracker The slot change tracker
     */
    abstract fun removeTracker(tracker: SlotChangeTracker)

    override fun viewedSlot(): ExtendedSlot = this

    override fun children(): List<AbstractInventory> = emptyList()

    @Deprecated(message = "Always returns itself.", replaceWith = ReplaceWith(""))
    override fun slots(): List<ExtendedSlot> = this.slots

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun getSlot(index: Int): Optional<Slot> =
            super<ExtendedSlot>.getSlot(index)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun offer(index: Int, stack: ItemStack): InventoryTransactionResult =
            super<ExtendedSlot>.offer(index, stack)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun safeSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            super<ExtendedSlot>.safeSet(index, stack)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun forceSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            super<ExtendedSlot>.forceSet(index, stack)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun pollFrom(index: Int): PollInventoryTransactionResult =
            super<ExtendedSlot>.pollFrom(index)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun pollFrom(index: Int, limit: Int): PollInventoryTransactionResult =
            super<ExtendedSlot>.pollFrom(index, limit)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun peekAt(index: Int): Optional<ItemStack> =
            super<ExtendedSlot>.peekAt(index)
}

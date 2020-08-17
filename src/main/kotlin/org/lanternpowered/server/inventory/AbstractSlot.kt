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
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
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

    override fun safeSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.safeSet(stack) else InventoryTransactionResults.rejectNoSlot(stack)

    override fun forceSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.forceSet(stack) else InventoryTransactionResults.rejectNoSlot(stack)

    override fun slotOrNull(index: Int): ExtendedSlot? =
            if (index == 0) this else null

    override fun slotIndexOrNull(slot: Slot): Int? =
            if (slot == this) 0 else null

    override fun getSlot(index: Int): Optional<Slot> =
            if (index == 0) this.asOptional() else emptyOptional()

    override fun peekAt(index: Int): Optional<ItemStack> =
            if (index == 0) this.peek().asOptional() else emptyOptional()

    override fun offer(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.offer(stack) else InventoryTransactionResults.rejectNoSlot(stack)

    override fun pollFrom(index: Int): PollInventoryTransactionResult =
            if (index == 0) this.poll() else InventoryTransactionResults.rejectPollNoSlot()

    override fun pollFrom(index: Int, limit: Int): PollInventoryTransactionResult =
            if (index == 0) this.poll(limit) else InventoryTransactionResults.rejectPollNoSlot()

    override fun slots(): List<ExtendedSlot> = listOf(this)
    override fun children(): List<AbstractMutableInventory> = emptyList()
    override fun capacity(): Int = 1
}

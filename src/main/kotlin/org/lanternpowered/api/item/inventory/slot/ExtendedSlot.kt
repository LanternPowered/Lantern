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

package org.lanternpowered.api.item.inventory.slot

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import java.util.Optional

typealias Slot = org.spongepowered.api.item.inventory.Slot

/**
 * An extended version of [Slot].
 */
interface ExtendedSlot : Slot, ExtendedInventory {

    override fun viewedSlot(): ExtendedSlot

    /**
     * The maximum stack size that can be stored, for the given [stack].
     *
     * In vanilla, most slots specify a limit of 64 items. But if the items
     * default max stack quantity is lower, the 64 items will be reduced to
     * that default.
     *
     * Different kind of implementations may handle this differently.
     */
    fun maxStackQuantity(stack: ItemStack): Int

    /**
     * Sets the given stack into this slot.
     *
     * This is the fast variant of [safeSet], use this if you don't care
     * about the transaction result.
     *
     * @param stack The stack of items to set (partially) into this slot
     * @return Whether the stack was completely added
     */
    fun safeSetFast(stack: ItemStack): Boolean

    /**
     * Sets the given stack into this slot.
     *
     * This is the fast variant of [forceSet], use this if you don't care
     * about the transaction result.
     *
     * @param stack The stack of items to set (partially) into this slot
     * @return Whether the stack was completely added
     */
    fun forceSetFast(stack: ItemStack): Boolean

    /**
     * Forcibly sets at [ItemStack] in this slot.
     *
     * This set method ignores any checks to [canContain] and forcibly puts
     * the stack in the slot.
     *
     * The stack can be partially rejected inventory stack is bigger than
     * the maximum slot stack quantity.
     *
     * @param stack The stack of items to set
     */
    @Deprecated(message = "Use forceSet.", replaceWith = ReplaceWith("this.forceSet(stack)"))
    override fun set(stack: ItemStack): InventoryTransactionResult =
            this.forceSet(stack)

    /**
     * Forcibly sets at [ItemStack] in this slot.
     *
     * This set method ignores any checks to [canContain] and forcibly puts
     * the stack in the slot.
     *
     * The stack can be partially rejected inventory stack is bigger than
     * the maximum slot stack quantity.
     *
     * @param stack The stack of items to set
     */
    fun forceSet(stack: ItemStack): InventoryTransactionResult

    /**
     * Safely sets at [ItemStack] in this slot.
     *
     * The stack can be partially rejected inventory stack is bigger than
     * the maximum slot stack quantity.
     *
     * @param stack The stack of items to set
     */
    fun safeSet(stack: ItemStack): InventoryTransactionResult

    /**
     * Tests whether the slot contains an item validated by the given
     * function. The function is also applied to empty items.
     */
    fun contains(fn: (ItemStackSnapshot) -> Boolean): Boolean

    // region Redundant slot functions

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun safeSet(index: Int, stack: ItemStack): InventoryTransactionResult

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun forceSet(index: Int, stack: ItemStack): InventoryTransactionResult

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun slotOrNull(index: Int): ExtendedSlot?

    @Deprecated(message = "Only returns 0 for this slot.")
    override fun slotIndexOrNull(slot: Slot): Int?

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun getSlot(index: Int): Optional<Slot>

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun peekAt(index: Int): Optional<ItemStack>

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun offer(index: Int, stack: ItemStack): InventoryTransactionResult

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun pollFrom(index: Int): PollInventoryTransactionResult

    @Deprecated(message = "Only works with index 0 for slots.")
    override fun pollFrom(index: Int, limit: Int): PollInventoryTransactionResult

    @Deprecated(message = "Always returns itself.")
    override fun slots(): List<ExtendedSlot>

    @Deprecated(message = "Is always empty.")
    override fun children(): List<ExtendedInventory>

    @Deprecated(message = "Is always 1.")
    override fun capacity(): Int

    // endregion
}

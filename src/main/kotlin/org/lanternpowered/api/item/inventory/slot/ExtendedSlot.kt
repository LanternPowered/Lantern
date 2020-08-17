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
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.Slot
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import java.util.Optional
import kotlin.contracts.contract

typealias EquipmentSlot = org.spongepowered.api.item.inventory.slot.EquipmentSlot
typealias FilteringSlot = org.spongepowered.api.item.inventory.slot.FilteringSlot
typealias FuelSlot = org.spongepowered.api.item.inventory.slot.FuelSlot
typealias InputSlot = org.spongepowered.api.item.inventory.slot.InputSlot
typealias OutputSlot = org.spongepowered.api.item.inventory.slot.OutputSlot
typealias SidedSlot = org.spongepowered.api.item.inventory.slot.SidedSlot

/**
 * Gets the normal slot as an extended slot.
 */
inline fun Slot.fix(): ExtendedSlot {
    contract { returns() implies (this@fix is ExtendedSlot) }
    return this as ExtendedSlot
}

/**
 * Gets the slot inventory as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedSlot.fix(): ExtendedSlot = this

/**
 * The transaction result used when attempting to poll an
 * item from a slot that doesn't exist.
 */
private val noSlotPollTransactionResult = InventoryTransactionResult.builder()
        .type(InventoryTransactionResultType.NO_SLOT)
        .poll(emptyItemStackSnapshot())
        .build()

/**
 * The transaction result used when attempting to offer or set an
 * item in a slot that doesn't exist.
 */
private fun noSlotRejectTransactionResult(stack: ItemStack): InventoryTransactionResult =
        InventoryTransactionResult.builder()
                .type(InventoryTransactionResultType.NO_SLOT)
                .reject(stack)
                .build()

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
    @Deprecated(message = "Use forcedSet.", replaceWith = ReplaceWith("this.forceSet(stack)"))
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

    // region Redundant slot functions

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun safeSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.safeSet(stack) else noSlotRejectTransactionResult(stack)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun forceSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.forceSet(stack) else noSlotRejectTransactionResult(stack)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun slot(index: Int): ExtendedSlot? =
            if (index == 0) this else null

    @Deprecated(message = "Only returns 0 for this slot.", replaceWith = ReplaceWith(""))
    override fun slotIndex(slot: Slot): Int? =
            if (slot == this) 0 else null

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun getSlot(index: Int): Optional<Slot> =
            if (index == 0) this.asOptional() else emptyOptional()

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun peekAt(index: Int): Optional<ItemStack> =
            if (index == 0) this.peek().asOptional() else emptyOptional()

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun offer(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.offer(stack) else noSlotRejectTransactionResult(stack)

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun pollFrom(index: Int): PollInventoryTransactionResult =
            if (index == 0) this.poll() else noSlotPollTransactionResult

    @Deprecated(message = "Only works with index 0 for slots.", replaceWith = ReplaceWith(""))
    override fun pollFrom(index: Int, limit: Int): PollInventoryTransactionResult =
            if (index == 0) this.poll(limit) else noSlotPollTransactionResult

    @Deprecated(message = "Always returns itself.", replaceWith = ReplaceWith(""))
    override fun slots(): List<ExtendedSlot> = listOf(this)

    @Deprecated(message = "Is always empty.", replaceWith = ReplaceWith(""))
    override fun children(): List<ExtendedInventory> = emptyList()

    @Deprecated(message = "Is always 1.", replaceWith = ReplaceWith(""))
    override fun capacity(): Int = 1

    // endregion
}

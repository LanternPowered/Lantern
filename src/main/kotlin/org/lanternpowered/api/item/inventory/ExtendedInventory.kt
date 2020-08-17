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

import org.lanternpowered.api.item.inventory.query.OneParamQueryType
import org.lanternpowered.api.item.inventory.query.Query
import org.lanternpowered.api.item.inventory.query.QueryTypes
import org.lanternpowered.api.item.inventory.query.TwoParamQueryType
import org.lanternpowered.api.item.inventory.query.of
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.KeyValueMatcher
import java.util.Optional
import java.util.function.Supplier
import kotlin.contracts.contract
import kotlin.reflect.KClass

typealias InventoryTransactionResult = org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult
typealias InventoryTransactionResultBuilder = org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Builder
typealias InventoryTransactionResultType = org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type
typealias PollInventoryTransactionResult = org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Poll
typealias Inventory = org.spongepowered.api.item.inventory.Inventory
typealias Slot = org.spongepowered.api.item.inventory.Slot

/**
 * Gets the normal inventories as an extended inventories.
 */
inline fun List<Inventory>.fix(): List<ExtendedInventory> =
        this.uncheckedCast()

/**
 * Gets the normal inventory as an extended inventory.
 */
inline fun Inventory.fix(): ExtendedInventory {
    contract { returns() implies (this@fix is ExtendedInventory) }
    return this as ExtendedInventory
}

/**
 * Gets the normal inventory as an extended inventory.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedInventory.fix(): ExtendedInventory = this

/**
 * Attempts to get the index for the given [slot] relative to this inventory.
 *
 * Throws an [IllegalArgumentException] if the given slot isn't part
 * of this inventory.
 */
inline fun Inventory.slotIndex(slot: Slot): Int {
    contract { returns() implies (this@slotIndex is ExtendedInventory) }
    return (this as ExtendedInventory).slotIndex(slot)
}

/**
 * Attempts to get the index for the given [slot] relative to this inventory.
 *
 * Returns `null` if the given slot isn't part of this inventory.
 */
inline fun Inventory.slotIndexOrNull(slot: Slot): Int? {
    contract { returns() implies (this@slotIndexOrNull is ExtendedInventory) }
    return (this as ExtendedInventory).slotIndexOrNull(slot)
}

/**
 * Similar to [ExtendedInventory.offer], but the quantity of the stack that was offered will
 * be reduced from the given stack. If the stack becomes empty, this means
 * that stack was added completely.
 *
 * @param stack A stack of items to offer
 * @return The transaction result which are the changes that occurred to the
 *      inventory
 */
inline fun Inventory.offerAndConsume(stack: ItemStack): InventoryTransactionResult {
    contract { returns() implies (this@offerAndConsume is ExtendedInventory) }
    return (this as ExtendedInventory).offerAndConsume(stack)
}

/**
 * Similar to [ExtendedInventory.offerFast], but the quantity of the stack that was
 * offered will be reduced from the given stack. If the stack becomes empty, this means
 * that stack was added completely.
 *
 * This is the fast variant of [offerAndConsume], use this if you don't care
 * about the transaction result.
 *
 * @param stack A stack of items to offer
 * @return Whether the stack is completely consumed
 */
inline fun Inventory.offerAndConsumeFast(stack: ItemStack): Boolean {
    contract { returns() implies (this@offerAndConsumeFast is ExtendedInventory) }
    return (this as ExtendedInventory).offerAndConsumeFast(stack)
}

/**
 * This is the fast variant of [ExtendedInventory.offer], use this if you don't care
 * about the transaction result.
 *
 * @param stacks The stacks of items to offer
 * @return Whether the stacks are all completely added
 */
inline fun Inventory.offerFast(vararg stacks: ItemStack): Boolean {
    contract { returns() implies (this@offerFast is ExtendedInventory) }
    return (this as ExtendedInventory).offerFast(stacks.asIterable())
}

/**
 * This is the fast variant of [ExtendedInventory.offer], use this if you don't care
 * about the transaction result.
 *
 * @param stacks The stacks of items to offer
 * @return Whether the stacks are all completely added
 */
inline fun Inventory.offerFast(stacks: Iterable<ItemStack>): Boolean {
    contract { returns() implies (this@offerFast is ExtendedInventory) }
    return (this as ExtendedInventory).offerFast(stacks)
}

/**
 * An extended version of [Inventory].
 */
interface ExtendedInventory : Inventory {

    /**
     * Gets the root inventory of this inventory.
     *
     * This is equivalent to calling [parent] until it
     * returns itself.
     */
    override fun root(): ExtendedInventory

    /**
     * Gets the parent inventory of this inventory.
     *
     * Returns itself if there is no parent (this is a top-level inventory)
     */
    override fun parent(): ExtendedInventory

    /**
     * Attempts to get the slot at the given [index].
     *
     * Throws an [IndexOutOfBoundsException] if the given index isn't
     * within the bounds of this inventory.
     */
    fun slot(index: Int): ExtendedSlot =
            this.slotOrNull(index) ?: throw IndexOutOfBoundsException(index)

    /**
     * Attempts to get the slot at the given [index].
     *
     * Returns `null` if the given index isn't within the bounds of this inventory.
     */
    fun slotOrNull(index: Int): ExtendedSlot?

    /**
     * Attempts to get the index for the given [slot] relative to this inventory.
     *
     * Throws an [IllegalArgumentException] if the given slot isn't part
     * of this inventory.
     */
    fun slotIndex(slot: Slot): Int =
            this.slotIndexOrNull(slot) ?: throw IllegalArgumentException("The slot $slot isn't part of this inventory.")

    /**
     * Attempts to get the index for the given [slot] relative to this inventory.
     *
     * Returns `null` if the given slot isn't part of this inventory.
     */
    fun slotIndexOrNull(slot: Slot): Int?

    override fun getSlot(index: Int): Optional<Slot> =
            this.slotOrNull(index).asOptional()

    override fun intersect(inventory: Inventory): ExtendedInventory
    override fun union(inventory: Inventory): ExtendedInventory

    /**
     * A list with all the children inventories of this inventory.
     */
    override fun children(): List<ExtendedInventory>

    /**
     * A list with all the slots of this inventory.
     */
    override fun slots(): List<ExtendedSlot>

    /**
     * Check whether the given item can be inserted into one of the slots of
     * this inventory. If this function returns false, it is guaranteed that
     * [offer] will fail for the given stack.
     */
    fun canContain(stack: ItemStack): Boolean

    /**
     * Similar to [offer], but the quantity of the stack that was offered will
     * be reduced from the given stack. If the stack becomes empty, this means
     * that stack was added completely.
     *
     * @param stack The stack of items to offer
     * @return The transaction result which are the changes that occurred to the
     *      inventory
     */
    fun offerAndConsume(stack: ItemStack): InventoryTransactionResult

    /**
     * Similar to [offer], but the quantity of the stack that was offered will
     * be reduced from the given stack. If the stack becomes empty, this means
     * that stack was added completely.
     *
     * @param stacks The stacks of items to offer
     * @return The transaction result which are the changes that occurred to the
     *      inventory
     */
    fun offerAndConsume(vararg stacks: ItemStack): InventoryTransactionResult =
            this.offerAndConsume(stacks.asIterable())

    /**
     * Similar to [offer], but the quantity of the stack that was offered will
     * be reduced from the given stack. If the stack becomes empty, this means
     * that stack was added completely.
     *
     * @param stacks The stacks of items to offer
     * @return The transaction result which are the changes that occurred to the
     *      inventory
     */
    fun offerAndConsume(stacks: Iterable<ItemStack>): InventoryTransactionResult

    /**
     * Similar to [offerFast], but the quantity of the stack that was offered will
     * be reduced from the given stack. If the stack becomes empty, this means
     * that stack was added completely.
     *
     * This is the fast variant of [offerAndConsume], use this if you don't care
     * about the transaction result.
     *
     * @param stack The stack of items to offer
     * @return Whether the stack is completely consumed
     */
    fun offerAndConsumeFast(stack: ItemStack): Boolean

    /**
     * This is the fast variant of [offer], use this if you don't care
     * about the transaction result.
     *
     * @param stacks The stacks of items to offer
     * @return Whether the stacks are all completely added
     */
    fun offerFast(vararg stacks: ItemStack): Boolean =
            this.offerFast(stacks.asIterable())

    /**
     * This is the fast variant of [offer], use this if you don't care
     * about the transaction result.
     *
     * @param stacks The stacks of items to offer
     * @return Whether the stacks are all completely added
     */
    fun offerFast(stacks: Iterable<ItemStack>): Boolean =
            stacks.all { stack -> this.offerFast(stack) }

    /**
     * This is the fast variant of [offer], use this if you don't care
     * about the transaction result.
     *
     * @param stack The stack of items to offer
     * @return Whether the stacks are all completely added
     */
    fun offerFast(stack: ItemStack): Boolean

    /**
     * Offers the given stacks to this inventory.
     *
     * @param stacks The stacks to offer to this inventory.
     * @return A success transaction-result if all stacks were added and
     *           failure when at least one stack was not or only partially added to the inventory.
     */
    override fun offer(vararg stacks: ItemStack): InventoryTransactionResult =
            this.offer(stacks.asIterable())

    /**
     * Offers the given stacks to this inventory.
     *
     * @param stacks The stacks to offer to this inventory.
     * @return A success transaction-result if all stacks were added and
     *           failure when at least one stack was not or only partially added to the inventory.
     */
    fun offer(stacks: Iterable<ItemStack>): InventoryTransactionResult

    /**
     * Offers the given stack to this inventory.
     *
     * @param stack The stack to offer to this inventory.
     * @return A success transaction-result if all stacks were added and
     *           failure when at least one stack was not or only partially added to the inventory.
     */
    fun offer(stack: ItemStack): InventoryTransactionResult

    /**
     * Gets and removes the first non empty stack from this inventory.
     *
     * This is the fast variant of [poll], use this if you don't care
     * about the transaction result.
     *
     * @return The polled stack, or empty if no items were polled
     */
    fun pollFast(): ItemStack

    /**
     * Gets and removes the first non empty stack from this inventory
     * that matches the given [predicate].
     *
     * This is the fast variant of [poll], use this if you don't care
     * about the transaction result.
     *
     * @return The polled stack, or empty if no items were polled
     */
    fun pollFast(predicate: (ItemStackSnapshot) -> Boolean): ItemStack

    /**
     * Gets and removes the first non empty stack from this inventory and
     * continues to poll items are similar until the limit is reached.
     *
     * This is the fast variant of [poll], use this if you don't care
     * about the transaction result.
     *
     * @return The polled stack, or empty if no items were polled
     */
    fun pollFast(limit: Int): ItemStack

    /**
     * Gets and removes the first non empty stack from this inventory
     * that matches the given [predicate] and continues to poll items
     * are similar until the limit is reached.
     *
     * This is the fast variant of [poll], use this if you don't care
     * about the transaction result.
     *
     * @return The polled stack, or empty if no items were polled
     */
    fun pollFast(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack

    /**
     * Gets and removes the first non empty stack from this inventory
     * that matches the given [predicate].
     *
     * @return A success transaction-result if an item was removed and
     *           failure when nothing was removed.
     */
    fun poll(predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult

    /**
     * Gets and removes the first non empty stack from this inventory
     * that matches the given [predicate] and continues to poll items
     * are similar until the limit is reached.
     *
     * @return A success transaction-result if an item was removed and
     *           failure when nothing was removed.
     */
    fun poll(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult

    /**
     * Peeks for the first non empty stack from this inventory
     * and continues to peek items are similar until the limit is reached.
     *
     * @return The peeked stack of items, or empty if none found
     */
    fun peek(limit: Int): ItemStack

    /**
     * Peeks for the first non empty stack from this inventory
     * that matches the given [predicate].
     *
     * @return The peeked stack of items, or empty if none found
     */
    fun peek(predicate: (ItemStackSnapshot) -> Boolean): ItemStack

    /**
     * Peeks for the first non empty stack from this inventory
     * that matches the given [predicate] and continues to peek items
     * are similar until the limit is reached.
     *
     * @return The peeked stack of items, or empty if none found
     */
    fun peek(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack

    /**
     * Peeks a result that would offer an [ItemStack] into this inventory.
     *
     * Peek operations do not modify the inventory, changes must be confirmed
     * through [PeekedTransactionResult.accept].
     *
     * @param stacks The stacks of items to offer
     * @return The peeked offer transaction result
     */
    fun peekOffer(vararg stacks: ItemStack): PeekedOfferTransactionResult =
            this.peekOffer(stacks.asIterable())

    /**
     * Peeks a result that would offer an [ItemStack] into this inventory.
     *
     * Peek operations do not modify the inventory, changes must be confirmed
     * through [PeekedTransactionResult.accept].
     *
     * @param stacks The stacks of items to offer
     * @return The peeked offer transaction result
     */
    fun peekOffer(stacks: Iterable<ItemStack>): PeekedOfferTransactionResult

    /**
     * Peeks a result that would offer an [ItemStack] into this inventory.
     *
     * Peek operations do not modify the inventory, changes must be confirmed
     * through [PeekedTransactionResult.accept].
     *
     * @param stack The stack of items to offer
     * @return The peeked offer transaction result
     */
    fun peekOffer(stack: ItemStack): PeekedOfferTransactionResult.Single

    /**
     * Forcibly sets at [ItemStack] at the given slot index.
     *
     * This set method ignores any checks to [canContain] and forcibly puts
     * the stack in the slot.
     *
     * The stack can be partially rejected inventory stack is bigger than
     * the maximum slot stack quantity.
     *
     * @param index The slot index to set the stack of items at
     * @param stack The stack of items to set
     */
    @Deprecated(message = "Use forceSet.", replaceWith = ReplaceWith("this.forceSet(index, stack)"))
    override fun set(index: Int, stack: ItemStack): InventoryTransactionResult =
            this.forceSet(index, stack)

    /**
     * Forcibly sets at [ItemStack] at the given slot index.
     *
     * This set function ignores any checks to [canContain] and forcibly puts
     * the stack in the slot.
     *
     * The stack can be partially rejected inventory stack is bigger than
     * the maximum slot stack quantity.
     *
     * @param index The slot index to set the stack of items at
     * @param stack The stack of items to set
     */
    fun forceSet(index: Int, stack: ItemStack): InventoryTransactionResult

    /**
     * Safely sets at [ItemStack] at the given slot index.
     *
     * The stack can be partially rejected inventory stack is bigger than
     * the maximum slot stack quantity.
     *
     * @param index The slot index to set the stack of items at
     * @param stack The stack of items to set
     */
    fun safeSet(index: Int, stack: ItemStack): InventoryTransactionResult

    override fun <T : Inventory> query(inventoryType: Class<T>): Optional<T> =
            this.query(inventoryType.kotlin).asOptional()

    /**
     * Query this inventory for a single inventory matching the supplied inventory type.
     *
     * This query will return `null` when the query didn't find a single inventory
     * matching the supplied inventory type.
     *
     * @param inventoryType The inventory type to query for
     * @param T The Type of inventory
     * @return The query result
     */
    fun <T : Inventory> query(inventoryType: KClass<T>): T?

    override fun query(query: Query): ExtendedInventory

    @JvmDefault
    override fun <P> query(queryType: Supplier<OneParamQueryType<P>>, param: P): ExtendedInventory =
            this.query(queryType.of(param))

    @JvmDefault
    override fun <P1, P2> query(queryType: Supplier<TwoParamQueryType<P1, P2>>, param1: P1, param2: P2): ExtendedInventory =
            this.query(queryType.of(param1, param2))

    @JvmDefault
    override fun query(matcher: KeyValueMatcher<*>): ExtendedInventory =
            this.query(QueryTypes.KEY_VALUE.of(matcher))
}

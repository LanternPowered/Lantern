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

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PeekedOfferTransactionResult
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.Slot
import org.lanternpowered.api.item.inventory.fix
import org.lanternpowered.api.item.inventory.query.Query
import org.lanternpowered.api.item.inventory.result.reject
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.stack.asSnapshot
import org.lanternpowered.api.item.inventory.stack.isNotEmpty
import org.lanternpowered.api.item.inventory.transaction.SlotTransaction
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.DataHolderBase
import org.lanternpowered.server.inventory.carrier.CarrierInventoryTypes
import org.lanternpowered.server.inventory.carrier.CarrierReference
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.type.ViewableInventory
import java.util.Optional
import kotlin.reflect.KClass

typealias TransactionConsumer = (SlotTransaction) -> Unit

abstract class AbstractInventory : ExtendedInventory, DataHolderBase {

    /**
     * The parent inventory of this inventory.
     */
    var parent: ExtendedInventory? = null

    /**
     * The carrier reference of this inventory, if applicable.
     */
    val carrierReference: CarrierReference<*>?

    init {
        val carrierType = CarrierInventoryTypes.getCarrierType(this::class.java)
        this.carrierReference = if (carrierType == null) null else CarrierReference.of(carrierType)
    }

    /**
     * Gets an empty inventory.
     */
    abstract fun empty(): LanternEmptyInventory

    /**
     * Sets the carrier of this inventory.
     */
    open fun setCarrier(carrier: Carrier?, override: Boolean = true) {
        if (this.carrierReference == null)
            return
        if (override || this.carrierReference.get() == null)
            this.carrierReference.set(carrier)
    }

    final override fun parent(): ExtendedInventory = this.parent ?: this
    final override fun root(): ExtendedInventory = this.parent?.root() ?: this
    final override fun hasChildren(): Boolean = this.children().isNotEmpty()

    override fun getSlot(index: Int): Optional<Slot> =
            this.slot(index).asOptional()

    final override fun asViewable(): Optional<ViewableInventory> =
            if (this is ViewableInventory) this.asOptional() else this.toViewable().asOptional()

    abstract override fun children(): List<AbstractInventory>

    /**
     * Adds a slot change listener to this inventory.
     *
     * @param listener The listener to add
     */
    abstract fun addSlotChangeListener(listener: (ExtendedSlot) -> Unit)

    /**
     * Attempts to convert this inventory into a [ViewableInventory].
     *
     * @return The viewable inventory
     */
    abstract fun toViewable(): ViewableInventory?

    /**
     * Attempts to peek offer the specified [ItemStack] to this inventory.
     *
     * The input stack will be (partially) consumed in the process.
     *
     * @param stack The item stack
     * @param transactionAdder The transaction adder, can be `null` if not collecting transactions
     */
    abstract fun peekOfferAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer? = null)

    /**
     * Attempts to offer the specified [ItemStack] to this inventory.
     *
     * The input stack will be (partially) consumed in the process.
     *
     * @param stack The item stack
     * @param transactionAdder The transaction adder, can be `null` if not collecting transactions
     */
    abstract fun offerAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer? = null)

    final override fun peekOffer(vararg stacks: ItemStack): PeekedOfferTransactionResult =
            this.peekOffer(stacks.asIterable())

    final override fun peekOffer(stack: ItemStack): PeekedOfferTransactionResult.Single {
        val copy = stack.copy()
        val transactions = mutableListOf<SlotTransaction>()
        this.peekOfferAndConsume(copy) { transaction -> transactions.add(transaction) }
        return LanternPeekedOfferSingleItemTransactionResult(transactions, copy.asSnapshot())
    }

    final override fun canFit(stack: ItemStack): Boolean {
        val quantity = stack.quantity
        this.peekOfferAndConsume(stack, null)
        val canFit = stack.isEmpty
        stack.quantity = quantity
        return canFit
    }

    final override fun offerFast(stack: ItemStack): Boolean =
            this.offerAndConsumeFast(stack.copy())

    final override fun offerAndConsumeFast(stack: ItemStack): Boolean {
        this.offerAndConsume(stack, null)
        return stack.isEmpty
    }

    final override fun offer(stack: ItemStack): InventoryTransactionResult {
        val quantity = stack.quantity
        val result = this.offerAndConsume0(stack)
        stack.quantity = quantity
        return result
    }

    final override fun offerAndConsume(stack: ItemStack): InventoryTransactionResult =
            this.offerAndConsume0(stack)

    private fun offerAndConsume0(stack: ItemStack): InventoryTransactionResult {
        val builder = InventoryTransactionResult.builder()
        this.offerAndConsume(stack) { transaction -> builder.transaction(transaction) }
        if (stack.isEmpty) {
            builder.type(InventoryTransactionResultType.SUCCESS)
        } else {
            builder.type(InventoryTransactionResultType.FAILURE).reject(stack.createSnapshot())
        }
        return builder.build()
    }

    final override fun offerFast(vararg stacks: ItemStack): Boolean =
            this.offerFast(stacks.asIterable())

    final override fun offerFast(stacks: Iterable<ItemStack>): Boolean {
        var success = true
        for (stack in stacks) {
            val quantity = stack.quantity
            this.offerAndConsume(stack, null)
            if (stack.isNotEmpty)
                success = false
            stack.quantity = quantity
        }
        return success
    }

    final override fun offer(vararg stacks: ItemStack): InventoryTransactionResult =
            this.offer(stacks.asIterable())

    final override fun offer(stacks: Iterable<ItemStack>): InventoryTransactionResult =
            this.offerAndConsume0(stacks.map(ItemStack::copy)) { stack -> stack.asSnapshot() }

    final override fun offerAndConsume(stacks: Iterable<ItemStack>): InventoryTransactionResult =
            this.offerAndConsume0(stacks) { stack -> stack.createSnapshot() }

    private fun offerAndConsume0(
            stacks: Iterable<ItemStack>, createSnapshot: (stack: ItemStack) -> ItemStackSnapshot
    ): InventoryTransactionResult {
        val transactions = mutableMapOf<Slot, SlotTransaction>()
        var rejected: MutableList<ItemStackSnapshot>? = null
        for (stack in stacks) {
            this.offerAndConsume(stack) { transaction ->
                transactions.compute(transaction.slot) { slot, previous ->
                    if (previous == null)
                        return@compute transaction
                    SlotTransaction(slot, previous.original, transaction.default)
                }
            }
            if (stack.isNotEmpty) {
                if (rejected == null)
                    rejected = mutableListOf()
                rejected.add(createSnapshot(stack))
            }
        }
        val builder = InventoryTransactionResult.builder()
        builder.transaction(transactions.values)
        if (rejected.isNullOrEmpty()) {
            builder.type(InventoryTransactionResultType.SUCCESS)
        } else {
            builder.type(InventoryTransactionResultType.FAILURE).reject(rejected)
        }
        return builder.build()
    }

    final override fun offerAndConsume(vararg stacks: ItemStack): InventoryTransactionResult =
            this.offerAndConsume(stacks.asIterable())

    final override fun peekOffer(stacks: Iterable<ItemStack>): PeekedOfferTransactionResult {
        val result = this.offer(stacks)
        result.revert()
        return LanternPeekedOfferMultipleItemsTransactionResult(
                result.slotTransactions, result.rejectedItems)
    }

    override fun safeSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            this.slot(index)?.safeSet(stack) ?: InventoryTransactionResults.rejectPollNoSlot()

    override fun forceSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            this.slot(index)?.forceSet(stack) ?: InventoryTransactionResults.rejectPollNoSlot()

    final override fun poll(): PollInventoryTransactionResult = this.poll { true }

    final override fun poll(limit: Int): PollInventoryTransactionResult = this.poll(limit) { true }

    final override fun pollFast(): ItemStack = this.pollFast { true }

    final override fun pollFast(limit: Int): ItemStack = this.pollFast(limit) { true }

    final override fun peek(): ItemStack = this.peek { true }

    final override fun peek(limit: Int): ItemStack = this.peek(limit) { true }

    final override fun query(query: Query): ExtendedInventory = query.execute(this).fix()

    final override fun <T : Inventory> query(inventoryType: KClass<T>): T? {
        if (inventoryType.isInstance(this))
            return this.uncheckedCast()
        return this.queryChild(inventoryType)
    }

    protected open fun <T : Inventory> queryChild(inventoryType: KClass<T>): T? {
        for (child in this.children()) {
            if (inventoryType.isInstance(child))
                return child.uncheckedCast()
        }
        for (child in this.children()) {
            val result = child.query(inventoryType)
            if (result != null)
                return result
        }
        return null
    }

    override fun offer(index: Int, stack: ItemStack): InventoryTransactionResult =
            this.slot(index)?.offer(stack) ?: InventoryTransactionResults.rejectNoSlot(stack)

    override fun pollFrom(index: Int): PollInventoryTransactionResult =
            this.slot(index)?.poll() ?: InventoryTransactionResults.rejectPollNoSlot()

    override fun pollFrom(index: Int, limit: Int): PollInventoryTransactionResult =
            this.slot(index)?.poll(limit) ?: InventoryTransactionResults.rejectPollNoSlot()

    override fun peekAt(index: Int): Optional<ItemStack> =
            this.slot(index)?.peek().asOptional()

    override fun <V : Any> get(key: Key<out Value<V>>): Optional<V> =
            super<DataHolderBase>.get(key)

    final override fun containsInventory(child: Inventory): Boolean {
        if (child == this)
            return true
        return this.children().any { inventory -> inventory.containsInventory(child) }
    }

    final override fun containsChild(child: Inventory): Boolean =
            this.children().contains(child)

    override fun <V : Any> get(child: Inventory, key: Key<out Value<V>>): Optional<V> {
        if (key == Keys.SLOT_INDEX && child is Slot)
            return this.slotIndex(child).asOptional().uncheckedCast()
        return child.get(key)
    }
}

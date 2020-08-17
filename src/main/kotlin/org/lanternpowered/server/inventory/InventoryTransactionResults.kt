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
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.item.inventory.result.reject

object InventoryTransactionResults {

    @JvmName("rejectSnapshot")
    fun reject(
            item: ItemStackSnapshot,
            type: InventoryTransactionResultType = InventoryTransactionResultType.FAILURE
    ): InventoryTransactionResult =
            InventoryTransactionResult.builder()
                    .type(type)
                    .reject(item)
                    .build()

    @JvmName("rejectSnapshots")
    fun reject(
            items: Iterable<ItemStackSnapshot>,
            type: InventoryTransactionResultType = InventoryTransactionResultType.FAILURE
    ): InventoryTransactionResult =
            InventoryTransactionResult.builder()
                    .type(type)
                    .reject(items)
                    .build()

    @JvmName("rejectSnapshots")
    fun reject(
            first: ItemStackSnapshot, vararg more: ItemStackSnapshot,
            type: InventoryTransactionResultType = InventoryTransactionResultType.FAILURE
    ): InventoryTransactionResult =
            reject(listOf(first) + more.asIterable(), type)

    fun reject(
            item: ItemStack,
            type: InventoryTransactionResultType = InventoryTransactionResultType.FAILURE
    ): InventoryTransactionResult = reject(item.createSnapshot(), type)

    fun reject(
            items: Iterable<ItemStack>,
            type: InventoryTransactionResultType = InventoryTransactionResultType.FAILURE
    ): InventoryTransactionResult = reject(items.map { it.createSnapshot() }, type)

    fun reject(
            first: ItemStack, vararg more: ItemStack,
            type: InventoryTransactionResultType = InventoryTransactionResultType.FAILURE
    ): InventoryTransactionResult = reject(listOf(first) + more.asIterable(), type)

    @JvmName("rejectSnapshot")
    fun rejectNoSlot(
            item: ItemStackSnapshot
    ): InventoryTransactionResult =
            reject(item, InventoryTransactionResultType.NO_SLOT)

    @JvmName("rejectSnapshots")
    fun rejectNoSlot(
            items: Iterable<ItemStackSnapshot>
    ): InventoryTransactionResult =
            reject(items, InventoryTransactionResultType.NO_SLOT)

    @JvmName("rejectSnapshots")
    fun rejectNoSlot(
            first: ItemStackSnapshot, vararg more: ItemStackSnapshot
    ): InventoryTransactionResult =
            reject(listOf(first) + more.asIterable(), InventoryTransactionResultType.NO_SLOT)

    fun rejectNoSlot(
            item: ItemStack
    ): InventoryTransactionResult =
            reject(item, InventoryTransactionResultType.NO_SLOT)

    fun rejectNoSlot(
            items: Iterable<ItemStack>
    ): InventoryTransactionResult =
            reject(items, InventoryTransactionResultType.NO_SLOT)

    fun rejectNoSlot(
            first: ItemStack, vararg more: ItemStack
    ): InventoryTransactionResult =
            reject(listOf(first) + more.asIterable(), InventoryTransactionResultType.NO_SLOT)

    private val rejectPoll = InventoryTransactionResult.builder()
            .type(InventoryTransactionResultType.FAILURE)
            .poll(emptyItemStackSnapshot())
            .build()

    private val rejectPollNoSlot = InventoryTransactionResult.builder()
            .type(InventoryTransactionResultType.NO_SLOT)
            .poll(emptyItemStackSnapshot())
            .build()

    fun rejectPoll(): PollInventoryTransactionResult = this.rejectPoll

    fun rejectPollNoSlot(): PollInventoryTransactionResult = this.rejectPollNoSlot
}

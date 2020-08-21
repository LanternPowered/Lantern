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
package org.lanternpowered.api.item.inventory.filter

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.item.inventory.stack.asSnapshot

/**
 * Converts this [ItemStack] filter function into an [ItemFilter].
 */
@JvmName("ofStack")
fun ((ItemStack) -> Boolean).asFilter(): ItemFilter =
        object : ItemFilter {
            override fun test(stack: ItemStack) = invoke(stack)
            override fun test(type: ItemType) = invoke(itemStackOf(type))
            override fun test(stack: ItemStackSnapshot) = invoke(stack.createStack())
        }

/**
 * Converts this [ItemStackSnapshot] filter function into an [ItemFilter].
 */
@JvmName("ofSnapshot")
fun ((ItemStackSnapshot) -> Boolean).asFilter(): ItemFilter =
        object : ItemFilter {
            override fun test(stack: ItemStack) = invoke(stack.asSnapshot())
            override fun test(type: ItemType) = invoke(itemStackOf(type).asSnapshot())
            override fun test(stack: ItemStackSnapshot) = invoke(stack)
        }

/**
 * Converts this [ItemType] filter function into an [ItemFilter].
 */
@JvmName("ofType")
fun ((ItemType) -> Boolean).asFilter(): ItemFilter =
        object : ItemFilter {
            override fun test(stack: ItemStack) = invoke(stack.type)
            override fun test(type: ItemType) = invoke(type)
            override fun test(stack: ItemStackSnapshot) = invoke(stack.type)
        }

/**
 * Represents a filter for [ItemType]s, [ItemStack]s and [ItemStackSnapshot]s.
 */
interface ItemFilter {

    /**
     * Tests whether the provided [ItemStack] is valid.
     *
     * @param stack The item stack
     * @return Whether the stack is valid
     */
    fun test(stack: ItemStack): Boolean

    /**
     * Tests whether the provided [ItemType] is valid.
     *
     * @param type The item type
     * @return Whether the type is valid
     */
    fun test(type: ItemType): Boolean

    /**
     * Tests whether the provided [ItemStackSnapshot] is valid.
     *
     * @param stack The item stack snapshot
     * @return Whether the stack is valid
     */
    fun test(stack: ItemStackSnapshot): Boolean

    /**
     * Gets this filter as an [ItemStack] function.
     */
    val forStack: (ItemStack) -> Boolean
        get() = this::test

    /**
     * Gets this filter as an [ItemStackSnapshot] function.
     */
    val forSnapshot: (ItemStackSnapshot) -> Boolean
        get() = this::test

    /**
     * Gets this filter as an [ItemType] function.
     */
    val forType: (ItemType) -> Boolean
        get() = this::test

    /**
     * Combines this [ItemFilter] with the other one. Both
     * [ItemFilter]s must succeed in order to get `true`
     * as a result.
     *
     * @param filter The item predicate
     * @return The combined item predicate
     */
    fun andThen(filter: ItemFilter): ItemFilter {
        val thisFilter = this
        return object : ItemFilter {
            override fun test(stack: ItemStack) = thisFilter.test(stack) && filter.test(stack)
            override fun test(type: ItemType) = thisFilter.test(type) && filter.test(type)
            override fun test(stack: ItemStackSnapshot) = thisFilter.test(stack) && filter.test(stack)
        }
    }

    /**
     * Inverts this [ItemFilter] as a new [ItemFilter].
     *
     * @return The inverted item filter
     */
    fun invert(): ItemFilter {
        val thisFilter = this
        return object : ItemFilter {
            override fun test(stack: ItemStack) = !thisFilter.test(stack)
            override fun test(type: ItemType) = !thisFilter.test(type)
            override fun test(stack: ItemStackSnapshot) = !thisFilter.test(stack)
        }
    }
}

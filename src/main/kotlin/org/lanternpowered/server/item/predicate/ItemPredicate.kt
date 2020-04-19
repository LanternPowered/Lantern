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
package org.lanternpowered.server.item.predicate

import org.lanternpowered.api.ext.itemStackOf
import org.lanternpowered.server.inventory.LanternItemStackSnapshot
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import java.util.function.Predicate

/**
 * Represents a predicate for [ItemType]s, [ItemStack]s and [ItemStackSnapshot]s.
 */
interface ItemPredicate {

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
     * Gets this [ItemPredicate] as a [ItemStack] [Predicate].
     *
     * @return The predicate
     */
    fun asStackPredicate(): Predicate<ItemStack> {
        return Predicate { this.test(it) }
    }

    /**
     * Gets this [ItemPredicate] as a [ItemStackSnapshot] [Predicate].
     *
     * @return The predicate
     */
    fun asSnapshotPredicate(): Predicate<ItemStackSnapshot> {
        return Predicate { this.test(it) }
    }

    /**
     * Gets this [ItemPredicate] as a [ItemType] [Predicate].
     *
     * @return The predicate
     */
    fun asTypePredicate(): Predicate<ItemType> {
        return Predicate { this.test(it) }
    }

    /**
     * Combines this [ItemPredicate] with the other one. Both
     * [ItemPredicate]s must succeed in order to get `true`
     * as a result.
     *
     * @param itemPredicate The item predicate
     * @return The combined item predicate
     */
    fun andThen(itemPredicate: ItemPredicate): ItemPredicate {
        val thisPredicate = this
        return object : ItemPredicate {
            override fun test(stack: ItemStack) = thisPredicate.test(stack) && itemPredicate.test(stack)
            override fun test(type: ItemType) = thisPredicate.test(type) && itemPredicate.test(type)
            override fun test(stack: ItemStackSnapshot) = thisPredicate.test(stack) && itemPredicate.test(stack)
        }
    }

    /**
     * Inverts this [ItemPredicate] as a new [ItemPredicate].
     *
     * @return The inverted item filter
     */
    fun invert(): ItemPredicate {
        val thisPredicate = this
        return object : ItemPredicate {
            override fun test(stack: ItemStack) = !thisPredicate.test(stack)
            override fun test(type: ItemType) = !thisPredicate.test(type)
            override fun test(stack: ItemStackSnapshot) = !thisPredicate.test(stack)
        }
    }

    companion object {

        /**
         * Constructs a [ItemPredicate] for the provided
         * [ItemStack] predicate.
         *
         * @param predicate The predicate
         * @return The item filter
         */
        @JvmStatic
        fun ofStackPredicate(predicate: Predicate<ItemStack>): ItemPredicate {
            return object : ItemPredicate {
                override fun test(stack: ItemStack) = predicate.test(stack)
                override fun test(type: ItemType) = predicate.test(itemStackOf(type))
                override fun test(stack: ItemStackSnapshot) = predicate.test(stack.createStack())
            }
        }

        /**
         * Constructs a [ItemPredicate] for the provided
         * [ItemStackSnapshot] predicate.
         *
         * @param predicate The predicate
         * @return The item filter
         */
        @JvmStatic
        fun ofSnapshotPredicate(predicate: Predicate<ItemStackSnapshot>): ItemPredicate {
            return object : ItemPredicate {
                override fun test(stack: ItemStack) = predicate.test(LanternItemStackSnapshot.wrap(stack))
                override fun test(type: ItemType) = predicate.test(LanternItemStackSnapshot.wrap(itemStackOf(type)))
                override fun test(stack: ItemStackSnapshot) = predicate.test(stack)
            }
        }

        /**
         * Constructs a [ItemPredicate] for the provided
         * [ItemType] predicate.
         *
         * @param predicate The predicate
         * @return The item filter
         */
        @JvmStatic
        fun ofTypePredicate(predicate: Predicate<ItemType>): ItemPredicate {
            return object : ItemPredicate {
                override fun test(stack: ItemStack) = predicate.test(stack.type)
                override fun test(type: ItemType) = predicate.test(type)
                override fun test(stack: ItemStackSnapshot) = predicate.test(stack.type)
            }
        }
    }
}

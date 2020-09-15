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

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.item.inventory.stack.asSnapshot
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
    operator fun invoke(stack: ItemStack): Boolean

    /**
     * Tests whether the provided [ItemType] is valid.
     *
     * @param type The item type
     * @return Whether the type is valid
     */
    operator fun invoke(type: ItemType): Boolean

    /**
     * Tests whether the provided [ItemStackSnapshot] is valid.
     *
     * @param stack The item stack snapshot
     * @return Whether the stack is valid
     */
    operator fun invoke(stack: ItemStackSnapshot): Boolean

    /**
     * Gets this [ItemPredicate] as a [ItemStack] [Predicate].
     *
     * @return The predicate
     */
    fun asStackPredicate(): (ItemStack) -> Boolean = { this(it) }

    /**
     * Gets this [ItemPredicate] as a [ItemStackSnapshot] [Predicate].
     *
     * @return The predicate
     */
    fun asSnapshotPredicate(): (ItemStackSnapshot) -> Boolean = { this(it) }

    /**
     * Gets this [ItemPredicate] as a [ItemType] [Predicate].
     *
     * @return The predicate
     */
    fun asTypePredicate(): (ItemType) -> Boolean = { this(it) }

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
            override fun invoke(stack: ItemStack) = thisPredicate(stack) && itemPredicate(stack)
            override fun invoke(type: ItemType) = thisPredicate(type) && itemPredicate(type)
            override fun invoke(stack: ItemStackSnapshot) = thisPredicate(stack) && itemPredicate(stack)
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
            override fun invoke(stack: ItemStack) = !thisPredicate(stack)
            override fun invoke(type: ItemType) = !thisPredicate(type)
            override fun invoke(stack: ItemStackSnapshot) = !thisPredicate(stack)
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
        fun ofStackPredicate(predicate: (ItemStack) -> Boolean): ItemPredicate {
            return object : ItemPredicate {
                override fun asStackPredicate(): (ItemStack) -> Boolean = predicate
                override fun invoke(stack: ItemStack) = predicate(stack)
                override fun invoke(type: ItemType) = predicate(itemStackOf(type))
                override fun invoke(stack: ItemStackSnapshot) = predicate(stack.createStack())
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
        fun ofSnapshotPredicate(predicate: (ItemStackSnapshot) -> Boolean): ItemPredicate {
            return object : ItemPredicate {
                override fun asSnapshotPredicate(): (ItemStackSnapshot) -> Boolean = predicate
                override fun invoke(stack: ItemStack) = predicate(stack.asSnapshot())
                override fun invoke(type: ItemType) = predicate(itemStackOf(type).asSnapshot())
                override fun invoke(stack: ItemStackSnapshot) = predicate(stack)
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
        fun ofTypePredicate(predicate: (ItemType) -> Boolean): ItemPredicate {
            return object : ItemPredicate {
                override fun asTypePredicate(): (ItemType) -> Boolean = predicate
                override fun invoke(stack: ItemStack) = predicate(stack.type)
                override fun invoke(type: ItemType) = predicate(type)
                override fun invoke(stack: ItemStackSnapshot) = predicate(stack.type)
            }
        }
    }
}

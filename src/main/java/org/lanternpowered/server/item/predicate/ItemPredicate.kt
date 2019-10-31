/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

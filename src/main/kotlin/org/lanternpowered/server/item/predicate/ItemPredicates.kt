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

import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.inventory.LanternItemStackSnapshot
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot

import java.util.function.Predicate

object ItemPredicates {

    /**
     * Constructs a [Predicate] that matches
     * [ItemStack]s with the given [ItemStack].
     *
     *
     * The provided item stack is not copied, so modifying the
     * stack will result in different predicate results.
     *
     * @param itemStack The item stack
     * @return The item predicate
     */
    @JvmStatic
    fun similarItemStack(itemStack: ItemStack): Predicate<ItemStack> {
        return similarItem(itemStack).asStackPredicate()
    }

    /**
     * Constructs a [Predicate] that matches
     * [ItemStack]s with the given [ItemStack].
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return The item predicate
     */
    @JvmStatic
    fun similarItemStack(itemStackSnapshot: ItemStackSnapshot): Predicate<ItemStack> {
        return similarItem(itemStackSnapshot).asStackPredicate()
    }

    /**
     * Constructs a [ItemPredicate] that matches
     * items with the given [ItemStack].
     *
     *
     * The provided item stack is not copied, so modifying the
     * stack will result in different predicate results.
     *
     * @param itemStack The item stack
     * @return The item predicate
     */
    @JvmStatic
    fun similarItem(itemStack: ItemStack): ItemPredicate {
        return SimilarItemPredicate(itemStack as LanternItemStack)
    }

    /**
     * Constructs a [ItemPredicate] that matches
     * items with the given [ItemStackSnapshot].
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return The item predicate
     */
    @JvmStatic
    fun similarItem(itemStackSnapshot: ItemStackSnapshot): ItemPredicate {
        return SimilarItemPredicate((itemStackSnapshot as LanternItemStackSnapshot).unwrap())
    }
}

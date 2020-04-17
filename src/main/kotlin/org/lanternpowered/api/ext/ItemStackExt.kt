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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackBuilder
import org.spongepowered.api.item.ItemType
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Will return {@code null} if this stack is empty.
 */
fun ItemStack.orNull(): ItemStack? = if (isEmpty) null else this

/**
 * Will return the given [ItemStack] if this stack is empty.
 */
fun ItemStack.orElse(itemStack: ItemStack): ItemStack = if (isEmpty) itemStack else this

/**
 * Will return the a supplied [ItemStack] if this stack is empty.
 */
fun ItemStack.orElse(supplier: () -> ItemStack): ItemStack {
    contract {
        callsInPlace(supplier, InvocationKind.AT_MOST_ONCE)
    }
    return if (isEmpty) supplier() else this
}

/**
 * Whether this item stack isn't empty.
 */
inline val ItemStack.isNotEmpty get() = !this.isEmpty

/**
 * Executes the given function if the stack isn't empty.
 */
inline fun ItemStack.ifNotEmpty(fn: (ItemStack) -> Unit) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (!isEmpty) fn(this)
}

/**
 * Constructs a new [ItemStack] with the given [ItemType], quantity and
 * possibility to apply other data using the function.
 */
inline fun itemStackOf(type: Supplier<out ItemType>, quantity: Int = 1, fn: ItemStackBuilder.() -> Unit = {}): ItemStack {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return itemStackOf(type.get(), quantity, fn)
}

/**
 * Constructs a new [ItemStack] with the given [ItemType], quantity and
 * possibility to apply other data using the function.
 */
inline fun itemStackOf(type: ItemType, quantity: Int = 1, fn: ItemStackBuilder.() -> Unit = {}): ItemStack {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return ItemStack.builder().itemType(type).quantity(quantity).apply(fn).build()
}

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

package org.lanternpowered.api.item.inventory.stack

import org.lanternpowered.api.item.inventory.ExtendedItemStack
import org.lanternpowered.api.item.inventory.ExtendedItemStackSnapshot
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import kotlin.contracts.contract

inline infix fun ItemStack.isSimilarTo(other: ItemStack): Boolean {
    contract {
        returns() implies (this@isSimilarTo is ExtendedItemStack)
    }
    return (this as ExtendedItemStack).isSimilarTo(other)
}

inline infix fun ItemStack.isSimilarTo(other: ItemStackSnapshot): Boolean {
    contract {
        returns() implies (this@isSimilarTo is ExtendedItemStack)
    }
    return (this as ExtendedItemStack).isSimilarTo(other)
}

inline infix fun ItemStackSnapshot.isSimilarTo(other: ItemStack): Boolean {
    contract {
        returns() implies (this@isSimilarTo is ExtendedItemStack)
    }
    return (this as ExtendedItemStackSnapshot).isSimilarTo(other)
}

inline infix fun ItemStackSnapshot.isSimilarTo(other: ItemStackSnapshot): Boolean {
    contract {
        returns() implies (this@isSimilarTo is ExtendedItemStackSnapshot)
    }
    return (this as ExtendedItemStackSnapshot).isSimilarTo(other)
}
